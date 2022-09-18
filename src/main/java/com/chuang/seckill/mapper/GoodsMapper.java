package com.chuang.seckill.mapper;

import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    GoodsDto findGoodsVobyGoodsId(Long goodsId);

    List<GoodsDto> findGoodsVo();

}
