package com.chuang.seckill.service;

import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.dto.OrderDeatilDto;
import com.chuang.seckill.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuang.seckill.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
public interface OrderService extends IService<Order> {



    /**
     * 秒杀
     *
     * @operation add
     * @date 1:44 下午 2022/3/4
     **/
    Order secKill(User user, Long goodsId);

    /**
     * 订单详情方法
     *
     * @param orderId
     * @date 10:21 下午 2022/3/6
     **/
    OrderDeatilDto detail(Long orderId);

    /**
     * 校验验证码
     * @return boolean
     **/
    boolean checkCaptcha(User tuser, Long goodsId, String captcha);

    /**
     * 获取秒杀地址
     **/
    String createPath(User tuser, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);
}
