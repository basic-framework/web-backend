package com.zl.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "TestDirectQueue") // 监听的队列名称 TestDirectQueue
public class MessageListener {

    @RabbitHandler
    public void process(String message) {
        System.out.println("DirectReceiver m消费者收到消息  : " + message);
    }
}