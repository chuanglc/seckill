package com.chuang.seckill.dto;

import com.chuang.seckill.entity.Goods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 *
 * @author: LC
 * @date 2022/3/3 5:43 下午
 * @ClassName: GoodsVo
 */
@ApiModel("商品返回对象")
@Data
public class GoodsDto extends Goods {

    /**
     * 秒杀价格
     **/
    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    /**
     * 剩余数量
     **/
    @ApiModelProperty("剩余数量")
    private Integer stockCount;

    /**
     * 开始时间
     **/
    @ApiModelProperty("开始时间")
    private Date startDate;

    /**
     * 结束时间
     **/
    @ApiModelProperty("结束时间")
    private Date endDate;

}
