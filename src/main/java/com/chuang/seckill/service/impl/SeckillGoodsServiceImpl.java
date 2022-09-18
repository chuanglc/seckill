package com.chuang.seckill.service.impl;

import com.chuang.seckill.common.LocalCache;
import com.chuang.seckill.controller.SeKillController;
import com.chuang.seckill.controller.SeckillGoodsController;
import com.chuang.seckill.entity.SeckillGoods;
import com.chuang.seckill.mapper.SeckillGoodsMapper;
import com.chuang.seckill.service.SeckillGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 秒杀商品表 服务实现类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Override
    public void getSeckillGoodsInfo() {
        List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(null);
        //加载到redis
        for (SeckillGoods seckillGood : seckillGoods) {
            Long goodsId = seckillGood.getGoodsId();
            String key = "seckillGood:" + goodsId;
            LocalCache.emptyStockMap.put(goodsId, false);
            redisTemplate.opsForValue().set(key,seckillGood.getStockCount(),2, TimeUnit.HOURS);
        }
    }
}
