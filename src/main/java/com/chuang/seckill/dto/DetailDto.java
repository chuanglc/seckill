package com.chuang.seckill.dto;

import com.chuang.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品详情返回对象
 *
 * @author: LC
 * @date 2022/3/6 10:06 上午
 * @ClassName: DetailVo
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailDto {


    private User user;

    private GoodsDto goodsVo;

    private int secKillStatus;

    private int remainSeconds;


}
