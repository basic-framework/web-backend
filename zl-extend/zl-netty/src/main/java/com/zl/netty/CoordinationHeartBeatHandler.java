package com.zl.netty;


import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 心跳检测
 * @Author GuihaoLv
 */
@Component
@Slf4j
public class CoordinationHeartBeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //当 Netty 的IdleStateHandler检测到连接超时（读 / 写空闲）时，会触发IdleStateEvent事件
        if (evt instanceof IdleStateEvent) {
            //读空闲（READER_IDLE）：客户端长时间未发送消息，判定为 “心跳超时”，可扩展为关闭连接或记录异常。
            //写空闲（WRITER_IDLE）：服务器长时间未发送消息，主动向客户端发送心跳包（"heart"），维持连接活性。
            IdleStateEvent e = (IdleStateEvent) evt;
            if(e.state()== IdleState.READER_IDLE){
                log.info("心跳超时");
            }else if(e.state()==IdleState.WRITER_IDLE){
                ctx.writeAndFlush("heart");
            }
        }
    }




}