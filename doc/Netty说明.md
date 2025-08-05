```
// 注入Netty的WebSocket处理器
@Autowired
private WebSocketServerHandler webSocketHandler;
//调用实例：
// 1. 获取目标用户的WebSocket连接
Channel channel = webSocketHandler.getUserChannel(userId);
    // 2. 构造消息（按约定格式封装为JSON）
    Map<String, Object> message = new HashMap<>();
        message.put("type", "notification"); // 消息类型（自定义，客户端需对应解析）
        message.put("content", content);     // 消息内容
        message.put("timestamp", System.currentTimeMillis()); // 时间戳
    // 3. 发送消息（封装为WebSocket文本帧）
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    // 发送失败时清理无效连接
            webSocketHandler.getUserChannel(userId); // 内部会检查并移除无效连接
```