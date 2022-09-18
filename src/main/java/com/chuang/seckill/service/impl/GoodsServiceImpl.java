package com.chuang.seckill.service.impl;

import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.entity.Goods;
import com.chuang.seckill.mapper.GoodsMapper;
import com.chuang.seckill.service.GoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;


    @Override
    public List<GoodsDto> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public GoodsDto findGoodsVobyGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVobyGoodsId(goodsId);
    }

}
