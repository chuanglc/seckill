package com.chuang.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chuang.seckill.annotation.AccessLimit;
import com.chuang.seckill.common.LocalCache;
import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.common.RespBeanEnum;
import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.dto.SeckillMessage;
import com.chuang.seckill.entity.Order;
import com.chuang.seckill.entity.SeckillOrder;
import com.chuang.seckill.entity.User;
import com.chuang.seckill.exception.GlobalException;
import com.chuang.seckill.rabbitmq.MQSender;
import com.chuang.seckill.service.GoodsService;
import com.chuang.seckill.service.OrderService;
import com.chuang.seckill.service.SeckillOrderService;
import com.chuang.seckill.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.wf.captcha.ArithmeticCaptcha;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author chuang
 * @Date 2022/07/27 08:08
 **/

@Slf4j
@Controller
@RequestMapping("/seckill")
@Api(value = "秒杀", tags = "秒杀")
public class SeKillController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MQSender mqSender;

    @ApiOperation("秒杀功能")
    @PostMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill (@PathVariable String path, User user, Long goodsId){
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //0.判断秒杀地址是否非法
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //1，通过redis控制，判断是否重复抢购 （线程安全）
        String key = "seckillOrder:" + goodsId+ ":" + user.getId();
        if(redisTemplate.hasKey(key)) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //2.如果库存大于0，通过redis进行预减库存， 如果库存小于0，返回-1 （线程不安全）
        //判断库存和执行预减，要为原子性操作，使用lua脚本

        //先通过map判断
        if(LocalCache.emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //再通过lua脚本判断
        String seckillGoodKey = "seckillGood:" + goodsId;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("stock.lua"));
        redisScript.setResultType(Long.class);

        Long stock = (Long) redisTemplate.execute(redisScript, List.of(seckillGoodKey));
        if ( stock < 0 ){
            LocalCache.emptyStockMap.put(goodsId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //3.判断实时库存


        //4.秒杀成功, mq异步生成订单
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));
//        mqSender.sendSeckillMessage(JsonUtil.object2Jso.nStr(seckillMessage));
        //先向前端返回消息
        return RespBean.success(0);
        //4.如果有用户未支付或者取消订单，那么加库存
    }


    /**
     * 获取秒杀结果
     *
     * @param goodsId
     * @return orderId 成功 ；-1 秒杀失败 ；0 排队中
     **/
    @ApiOperation("获取秒杀结果")
    @GetMapping("getResult")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }


    /**
     * 生成验证码
     * @param tUser
     * @param goodsId
     * @param response
     */
    @ApiOperation("获取验证码")
    @GetMapping(value = "/captcha")
    public void verifyCode(User tUser, Long goodsId, HttpServletResponse response) {
        if (tUser == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + tUser.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }


    /**
     * 获取秒杀地址
     * 校验验证码
     **/
    @ApiOperation("获取秒杀地址")
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @GetMapping(value = "/path")
    @ResponseBody
    public RespBean getPath(User tuser, Long goodsId, String captcha, HttpServletRequest request) {
        if (tuser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        boolean check = orderService.checkCaptcha(tuser, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(tuser, goodsId);
        return RespBean.success(str);
    }


}
