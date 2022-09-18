package com.chuang.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 *
 * @author: LC
 * @date 2022/3/7 7:42 下午
 * @ClassName: MQSender
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送秒杀信息
     **/
    public void sendSeckillMessage(String message) {
        log.info("发送消息" + message);
        //因为交换机和队列通过routingKey绑定了，发送消息的时候通过交换机和routingKey，消费的时候通过queue就可以了
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);

    }

}
