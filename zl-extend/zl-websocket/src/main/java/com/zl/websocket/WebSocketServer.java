package com.zl.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务器端组件
 * @Author GuihaoLv
 */
@Component
//标记这个类是一个WebSocket端点，可以接收来自客户端的WebSocket连接请求。/ws/{sid}表示WebSocket的URL路径模式，其中{sid}是路径参数，代表会话ID（Session ID）。
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {
    //这里声明了一个静态的Map，用来存储每个连接的Session对象，键是sid，值是对应的Session。通过这种方式，服务器可以追踪每一个连接的客户端。
    private static Map<String, Session> sessionMap = new ConcurrentHashMap();
    //一个线程安全的集合，用来存储所有活动的WebSocketServer实例。
    public static CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();

    //@OnOpen：当一个新的WebSocket连接成功建立时，此方法会被调用。
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        System.out.println("客户端：" + userId + "建立连接");
        webSockets.add(this);
        sessionMap.put(userId, session);//把新的Session对象存入sessionMap中。
    }


    //每当从客户端接收到消息时，该方法就会触发。这里只是简单地打印出收到的消息。
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        System.out.println("收到来自客户端：" + userId + "的信息:" + message);
    }


//    @OnMessage
//    public void onMessage(String message, @PathParam("userId") String userId) {
//        System.out.println("收到来自客户端：" + userId + "的信息:" + message);
//        try {
//            // 使用 FastJSON 解析 JSON 字符串
//            JSONObject jsonMessage = JSON.parseObject(message);
//            String toUserId = jsonMessage.getString("toUserId");
//
//            // 获取目标用户的会话
//            Session targetSession = sessionMap.get(toUserId);
//
//            if (targetSession != null && targetSession.isOpen()) {
//                System.out.println("正在向用户：" + toUserId + "发送消息");
//                // 将消息转发给目标用户
//                targetSession.getBasicRemote().sendText(jsonMessage.toJSONString());
//            } else {
//                System.out.println("无法找到目标用户或连接已关闭：" + toUserId);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    //当一个WebSocket连接关闭时，此方法会被调用。它会从sessionMap中移除相应的Session对象，并输出一条日志信息。
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        System.out.println("连接断开:" + userId);
        webSockets.remove(this);
        sessionMap.remove(userId);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket 发生错误: " + error.getMessage());
        error.printStackTrace();
    }


     /**
     * 广播
     * @param message
     */
    //遍历所有现存的Session对象，并尝试向每个客户端发送文本消息。如果发送过程中遇到任何异常，就捕获异常并打印堆栈跟踪信息。
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("content", message); // 原始内容放在content字段
        jsonMessage.put("timestamp", System.currentTimeMillis());
        jsonMessage.put("status", "success");
        System.out.println("广播消息"+jsonMessage.toJSONString()+"到"+sessions.size()+"个客户端");
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(jsonMessage.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


     /**
     * 向指定客户端发送文本消息（指定发送方）
     * @param fromUserId 发送方
     * @param toUserId 接收方
     * @param message
     */
    public void sendToClient(String fromUserId,String toUserId,String message) {
        try {
            // 创建标准消息结构
            JSONObject messageJson = new JSONObject();
            messageJson.put("content", message); // 原始内容放在content字段
            messageJson.put("timestamp", System.currentTimeMillis());
            messageJson.put("status", "success");
            messageJson.put("from",fromUserId);
            System.out.println( fromUserId+"发送消息"+messageJson.toJSONString()+"到"+toUserId);
            Session session = sessionMap.get(toUserId);
            if (session != null && session.isOpen()) {
                // 发送序列化的JSON字符串
                session.getBasicRemote().sendText(messageJson.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 系统向某个用户发送消息(无指定发送方)
     * @param toUserId
     * @param message
     */
    public void sendToClient(String toUserId,String message) {
        try {
            // 创建标准消息结构
            JSONObject messageJson = new JSONObject();
            messageJson.put("content", message); // 原始内容放在content字段
            messageJson.put("timestamp", System.currentTimeMillis());
            messageJson.put("status", "success");
            System.out.println("发送消息"+messageJson.toJSONString()+"到"+toUserId);
            Session session = sessionMap.get(toUserId);
            if (session != null && session.isOpen()) {
                // 发送序列化的JSON字符串
                session.getBasicRemote().sendText(messageJson.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}