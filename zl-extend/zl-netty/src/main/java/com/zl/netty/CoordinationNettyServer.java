package com.zl.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
/**
 * 基于 Netty 的 WebSocket 服务器配置类
 * @Author: GuihaoLv
 */
@Configuration
public class CoordinationNettyServer {

    @Autowired
    private CoordinationSocketHandler coordinationSocketHandler;
    @Autowired
    private CoordinationHeartBeatHandler coordinationHeartBeatHandler;


    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); //用于处理客户端的连接请求,在 Netty 中，bossGroup 会为每个连接创建一个 Channel
        EventLoopGroup group = new NioEventLoopGroup();//用于处理已连接的客户端的 I/O 事件（例如读写操作）。每个 Channel 会被分配到 group 的线程上。
        try {
            ServerBootstrap sb = new ServerBootstrap(); //服务器启动器
            sb.option(ChannelOption.SO_BACKLOG, 1024); //设置 TCP 连接队列大小，应对高并发连接请求
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(8004)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { //通道处理器链
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            //以块的方式来写的处理器
                            ch.pipeline().addLast(new ChunkedWriteHandler()); //支持大文件分块写入
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));//聚合 HTTP 消息，最大聚合长度。
                            //协议升级为WS协议
                            //核心 WebSocket 处理器
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws", "WebSocket", true, 65536 * 10));
                            ch.pipeline().addLast(coordinationSocketHandler);//自定义消息处理类
                           // ch.pipeline().addLast(coordinationHeartBeatHandler);//自定义心跳处理类
                        }
                    });
            ChannelFuture cf = sb.bind().sync(); //异步绑定端口，sync()阻塞等待绑定完成。
            System.out.println(CoordinationNettyServer.class + "已启动，正在监听： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }




}