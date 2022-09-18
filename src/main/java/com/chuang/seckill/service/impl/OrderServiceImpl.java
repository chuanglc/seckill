package com.chuang.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chuang.seckill.common.RespBeanEnum;
import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.dto.OrderDeatilDto;
import com.chuang.seckill.entity.Order;
import com.chuang.seckill.entity.SeckillGoods;
import com.chuang.seckill.entity.SeckillOrder;
import com.chuang.seckill.entity.User;
import com.chuang.seckill.exception.GlobalException;
import com.chuang.seckill.mapper.OrderMapper;
import com.chuang.seckill.service.GoodsService;
import com.chuang.seckill.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuang.seckill.service.SeckillGoodsService;
import com.chuang.seckill.service.SeckillOrderService;
import com.chuang.seckill.utils.MD5Util;
import com.chuang.seckill.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    @Transactional
    public Order secKill(User user, Long goodsId) {
        //1.判断库存，此时是在mysql中获取秒杀商品的实时库存。
        GoodsDto goodsDto = goodsService.findGoodsVobyGoodsId(goodsId);
        if (goodsDto.getStockCount() < 1) {
            return;
        }
        log.info("此时是在mysql中获取实时库存：" + goodsDto.getStockCount());
        //1.减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsDto.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //只能对库存大于 0的秒杀商品进行减一
        boolean update = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = " + "stock_count-1")
                .eq("goods_id", goodsDto.getId())
                .gt("stock_count", 0)
        );

        log.info("seckill:" + update);

        //判断是否还有库存
        if (seckillGoods.getStockCount() < 1) {
            log.info("seckill  判断是否还有库存:" + seckillGoods.getStockCount());
            redisTemplate.opsForValue().set("isStockEmpty:" + goodsDto.getId(), "0");
        }

        //2.生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsDto.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsDto.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(LocalDateTime.now());
        orderMapper.insert(order);

        //3.生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsDto.getId());
        seckillOrderService.save(seckillOrder);

        //4.修改redis个人购买记录
        String key = "seckillOrder:" + goodsDto.getId()+ ":" + user.getId();
        redisTemplate.opsForValue().set(key,1,2, TimeUnit.HOURS);
        return order;
    }

    @Override
    public OrderDeatilDto detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }

        Order tOrder = orderMapper.selectById(orderId);
        GoodsDto goodsVobyGoodsId = goodsService.findGoodsVobyGoodsId(tOrder.getGoodsId());
        OrderDeatilDto orderDeatilVo = new OrderDeatilDto();
        orderDeatilVo.setTOrder(tOrder);
        orderDeatilVo.setGoodsVo(goodsVobyGoodsId);
        return orderDeatilVo;
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(captcha)) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 1, TimeUnit.MINUTES);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }
}
