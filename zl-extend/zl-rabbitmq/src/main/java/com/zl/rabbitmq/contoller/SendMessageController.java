package com.zl.rabbitmq.contoller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mq")
public class SendMessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        // 发送String类型消息到指定交换机和路由键
        String message = "你好，这是一条字符串消息";
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", message);
        return "ok";
    }

}