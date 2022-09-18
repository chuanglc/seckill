package com.chuang.seckill.rabbitmq;
import com.alibaba.fastjson.JSON;
import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.common.RespBeanEnum;
import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.dto.SeckillMessage;
import com.chuang.seckill.entity.SeckillOrder;
import com.chuang.seckill.entity.User;
import com.chuang.seckill.service.GoodsService;
import com.chuang.seckill.service.OrderService;
import com.chuang.seckill.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 消息消费者
 *
 * @author: LC
 * @date 2022/3/7 7:44 下午
 * @ClassName: MQReceiver
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private GoodsService goodsServicel;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;


    /**
     * 下单操作

     **/
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接收消息：" + message);
        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);
        //下单操作
        orderService.secKill(seckillMessage.getTUser(), seckillMessage.getGoodsId());
    }
}
