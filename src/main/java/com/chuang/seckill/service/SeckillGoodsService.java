package com.chuang.seckill.service;

import com.chuang.seckill.entity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuang.seckill.entity.SeckillOrder;

import java.util.List;

/**
 * <p>
 * 秒杀商品表 服务类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
public interface SeckillGoodsService extends IService<SeckillGoods> {

    void getSeckillGoodsInfo();

}
