package com.chuang.seckill.controller;


import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.common.RespBeanEnum;
import com.chuang.seckill.dto.OrderDeatilDto;
import com.chuang.seckill.entity.User;
import com.chuang.seckill.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */

@RestController
@RequestMapping("/order")
@Api(value = "订单", tags = "订单")

public class OrderController {

    @Autowired
    private OrderService orderService;


    @ApiOperation("订单")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public RespBean detail(User tUser, Long orderId) {
        if (tUser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDeatilDto orderDeatilVo = orderService.detail(orderId);
        return RespBean.success(orderDeatilVo);
    }
}

