package com.zl.netty;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 了基于 Netty 的 WebSocket 服务端核心处理逻辑，
 * 用于处理客户端的 WebSocket 消息和事件。
 * @Author GuihaoLv
 */
@Configuration
@Slf4j
@ChannelHandler.Sharable// 说明该处理器（CoordinationSocketHandler）是线程安全的，可以在多个 Channel 之间共享。
//TextWebSocketFrame 表示 WebSocket 中的文本帧消息。
public class CoordinationSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    //用于保存所有连接的客户端 Channel，可用于广播消息
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //使用一个 HashMap 存储用户 ID 和对应的 Channel 映射，用于点对点消息通信。
    private final Map<String, Channel> cmap = new ConcurrentHashMap<>();

    //当客户端与服务端建立连接时触发。
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel=ctx.channel();
        channelGroup.add(channel);
        System.out.println(ctx.channel().id()+"会话建立连接，通道开启！");
    }

    //当客户端与服务端断开连接时触发。
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.remove(channel);
        String offlineUserId = null;
        for (Map.Entry<String, Channel> entry : cmap.entrySet()) {
            if (Objects.equals(entry.getValue(), channel)) {
                offlineUserId = entry.getKey();
                cmap.remove(offlineUserId);
                break;
            }
        }
        System.out.println(ctx.channel().id()+"会话断开连接，通道关闭！");
    }


     /**
     * 用于处理客户端发送的 WebSocket 消息
     * @param ctx  通道处理器上下文
     * @param msg  WebSocket文本帧消息。
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //接收的消息
        Map map = JSON.parseObject(msg.text(), Map.class); //将收到的消息转为 JSON。
        String type = map.get("type").toString(); //据消息中的 type 字段决定调用不同的业务方法。
        switch (type) {
            case "1":   // 登录
                websocketLogin(map, ctx);
                break;
            case "2":   // 发送消息
                send(map);
                break;
//            case "3":   // 结束通话
//                endCall(map);
//                break;
            default:
                log.warn("未知的 WebSocket 消息类型：{}", type);
                break;
        }
        System.out.println(String.format("收到客户端%s的数据：%s", ctx.channel().id(), msg.text()));
    }

    /**
     * 登录建立WebSocket连接
     * @param map
     * @param ctx
     */
    private void websocketLogin(Map map,ChannelHandlerContext ctx) {
        String userId=map.get("userId").toString();
        System.out.println(userId+"用户和服务器建立连接");
        cmap.put("user:"+userId,ctx.channel());
        channelGroup.add(ctx.channel());
        System.out.println(userId+"登录");
    }


    /**
     * 发送消息
     * @param
     * @throws InterruptedException
     */
    private void send(Map map) {
        String toUserId=map.get("toUserId").toString();
        if (cmap.containsKey("user:"+toUserId)){
            Channel channel = cmap.get("user:" +toUserId );
            Map<String,Object> obj = new HashMap<>();
            obj.put("type",2); // 对话
            obj.put("content",map.get("content"));
            obj.put("time", System.currentTimeMillis());
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));// 会将数据发送到客户端。
        }else {
            System.out.println("未登录");
        }
    }





//    private void endCall(Map map) {
//        String fromUserId = map.get("userId").toString();
//        String toUserId = map.get("toUserId").toString();
//        if (cmap.containsKey("user:" + toUserId)) {
//            Channel channel = cmap.get("user:" + toUserId);
//            Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 3); // 通话结束
//            obj.put("time", System.currentTimeMillis());
//            obj.put("fromUserId", fromUserId);
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));
//        } else {
//            System.out.println("未登录");
//        }
//
//        if (cmap.containsKey("user:" + fromUserId)) {
//            Channel channel = cmap.get("user:" + fromUserId);
//            Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 3); // 通话结束
//            obj.put("time", System.currentTimeMillis());
//            obj.put("fromUserId", fromUserId);
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));
//        }
//
//
//    }


    /**
     * 发送消息(指定发送方)
     * @param fromUserId
     * @param toUserId
     * @param message
     */
    public void sendToUser(String fromUserId,String toUserId, String message) {
        Channel channel = cmap.get("user:"+toUserId);
        Map<String,Object> obj = new HashMap<>();
        obj.put("type",2); // 对话
        obj.put("fromUserId", fromUserId);
        obj.put("content",message);
        obj.put("time", System.currentTimeMillis());
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));
        } else {
            log.warn("用户 {} 不在线，无法发送消息",toUserId);
        }
    }

    /**
     * 发送消息(不指定发送方)
     * @param toUserId
     * @param message
     */
    public void sendToUserWithNo(String toUserId, String message) {
        Channel channel = cmap.get("user:"+toUserId);
        Map<String,Object> obj = new HashMap<>();
        obj.put("type",2); // 对话
        obj.put("content",message);
        obj.put("time", System.currentTimeMillis());
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));
        } else {
            log.warn("用户 {} 不在线，无法发送消息",toUserId);
        }
    }

    /**
     * 发送消息(广播)
     * @param message
     */
    public void sendToAll(String message) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }


    /**
     * 绑定用户ID与连接
     * 场景：用户登录成功后调用，建立"用户-连接"映射
     * @param userId 用户唯一标识
     * @param channel 该用户的WebSocket连接
     */
    public void bindUserChannel(String userId, Channel channel) {
        // 校验参数合法性
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("绑定用户连接失败：用户ID为空");
            return;
        }
        if (channel == null || !channel.isActive()) {
            log.warn("绑定用户连接失败：连接无效 [用户ID: {}]", userId);
            return;
        }

        // 处理重复登录：若用户已绑定其他连接，先关闭旧连接
        Channel oldChannel = cmap.get(userId);
        if (oldChannel != null && oldChannel != channel) {
            log.info("用户重复登录，关闭旧连接 [用户ID: {}, 旧连接ID: {}]",
                    userId, oldChannel.id().asShortText());
            oldChannel.close(); // 关闭旧连接（会触发onChannelInactive清理）
        }

        // 绑定新连接
        cmap.put(userId, channel);
        log.info("用户连接绑定成功 [用户ID: {}, 连接ID: {}]",
                userId, channel.id().asShortText());
    }


    /**
     * 根据用户ID获取对应的连接
     * 场景：点对点消息发送时调用
     * @param userId 用户唯一标识
     * @return 该用户的WebSocket连接（若离线则返回null）
     */
    public Channel getUserChannel(String userId) {
        if (userId == null) {
            return null;
        }
        Channel channel = cmap.get(userId);
        // 检查连接是否活跃，自动清理无效连接
        if (channel != null && !channel.isActive()) {
            cmap.remove(userId);
            log.warn("用户连接已失效，自动清理 [用户ID: {}]", userId);
            return null;
        }
        return channel;
    }


    /**
     * 主动断开用户连接
     * 场景：管理员强制下线用户时调用
     * @param userId 用户唯一标识
     */
    public void disconnectUser(String userId) {
        Channel channel = getUserChannel(userId);
        if (channel != null && channel.isActive()) {
            channel.close(); // 关闭连接（会触发onChannelInactive清理）
            log.info("主动断开用户连接 [用户ID: {}]", userId);
        } else {
            log.warn("断开用户连接失败：用户未在线 [用户ID: {}]", userId);
        }
    }


    /**
     * 获取当前在线用户数
     */
    public int getOnlineUserCount() {
        return cmap.size();
    }


    /**
     * 获取当前总连接数（包含未登录的客户端）
     */
    public int getTotalConnectionCount() {
        return channelGroup.size();
    }




}







