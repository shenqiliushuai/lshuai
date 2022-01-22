package com.les.ls.config;

import com.les.ls.utils.Thread.ReadLogThead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * websocket配置
 */
@Slf4j
@Component
@ServerEndpoint("/websocket")
public class WebSocketServer {
    /**
     * websocket连接数
     */
    private final AtomicInteger connectionsNum = new AtomicInteger(0);
    /**
     * 存储连接对象
     */
    public static final Map<String, Session> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 读取日志任务执行器
     */
    private ExecutorService executorService;

    @OnOpen
    public void onOpen(Session session) {
        //连接数量+1
        connectionsNum.addAndGet(1);
        webSocketMap.put(session.getId(), session);
        log.debug("新连接加入，当前在线数量->{}", connectionsNum.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("session->{}:接受到客户端消息msg->{}", session.getId(), message);
        try {
            if (executorService == null || executorService.isShutdown()) {
                executorService = Executors.newSingleThreadExecutor();
            }
            executorService.execute(new ReadLogThead(session));
        } catch (Exception e) {
            log.error("session->{}:发送消息异常！", session.getId(), e);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("session->{}:连接发生错误！", session.getId(), e);
    }

    @OnClose
    public void onClose(Session session) {
        log.debug("session->{}:正在关闭连接...", session.getId());
        try {
            connectionsNum.addAndGet(-1);
            webSocketMap.remove(session.getId());
            session.close();
            log.debug("连接已经关闭...");
        } catch (Exception e) {
            log.error("关闭webSocket连接出现异常！", e);
        }
    }
}
