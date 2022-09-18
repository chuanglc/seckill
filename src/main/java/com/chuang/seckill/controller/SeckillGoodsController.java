package com.chuang.seckill.controller;


import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.entity.SeckillGoods;
import com.chuang.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 秒杀商品表 前端控制器
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Autowired
    SeckillGoodsService seckillGoodsService;

    /**
     * 把秒杀商品库存数量加载到Redis
     */
    @GetMapping("/loadseckillgoods")
    public RespBean loadRedisSeckillGoods(){
        seckillGoodsService.getSeckillGoodsInfo();
        return RespBean.success();
    }
}

