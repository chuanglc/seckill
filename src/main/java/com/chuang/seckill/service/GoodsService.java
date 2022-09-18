package com.chuang.seckill.service;

import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
public interface GoodsService extends IService<Goods> {

    /**
     * 返回商品列表
     *
     * @param
     * @return java.util.List<com.example.seckilldemo.vo.GoodsVo>
     * @author LC
     * @operation add
     * @date 5:49 下午 2022/3/3
     **/
    List<GoodsDto> findGoodsVo();

    /**
     * 获取商品详情
     *
     * @param goodsId
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 9:37 上午 2022/3/4
     **/
    GoodsDto findGoodsVobyGoodsId(Long goodsId);
}
