package com.zl.rabbitmq.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mq")
@Tag(name = "RabbitMQ消息接口", description = "RabbitMQ消息发送相关接口")
public class SendMessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendDirectMessage")
    @Operation(summary = "发送直接消息", description = "向指定交换机和路由键发送字符串消息")
    public String sendDirectMessage() {
        // 发送String类型消息到指定交换机和路由键
        String message = "你好，这是一条字符串消息";
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", message);
        return "ok";
    }

}