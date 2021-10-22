package com.bcp.serverc.controller;

import com.bcp.general.model.BcpUserModel;
import com.bcp.serverc.config.WebSocketConfig;
import com.bcp.serverc.model.BcpTask;
import com.bcp.serverc.service.impl.BcpTaskServiceImpl;
import com.bcp.serverc.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//@ConditionalOnClass(value = WebSocketConfig.class)
//@Transactional(readOnly = false, rollbackFor = Exception.class)
@ServerEndpoint(value = "/webSocket/{connect_message}", configurator = WebSocketConfig.class)
@Component
public class WebSocketServer {
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    // private volatile static AtomicInteger onlineNum = new AtomicInteger();

    // concurrent包的线程安全Set，用来存放每个客户端userId对应的WebSocketServer对象。
    // sessionPool足以承担用户数量
    // 理想情况应该是<String, Map<String, Session>>的userId -> (taskId ->
    // session)的map
    // 这样用户可以同时参与计算多个任务而不冲突，但这样用户连接如果不发送taskId或发送错误taskId的情况也需要考虑
    private static volatile ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String, HttpSession> loginUsers = new ConcurrentHashMap<>();

    @Autowired
    private static BcpTaskServiceImpl bcpTaskSrv;

    public static UserServiceImpl userService;

    /**
     * 多实例websocket必须通过这种方式注入
     *
     * @param bcpTaskSrv
     */
    @Autowired
    public void setBcpTaskSrv(BcpTaskServiceImpl bcpTaskSrv) {
        WebSocketServer.bcpTaskSrv = bcpTaskSrv;
    }

    // 发送消息
    public void sendMessage(Session session, Object message) throws IOException, EncodeException {
        if (session != null) {
            synchronized (session) {
                // System.out.println("发送数据：" + message);
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(json);
                // session.getBasicRemote().sendObject(json);
            }
        }
    }

    /**
     * 给指定用户发送信息
     *
     * @param userId
     * @param message
     */
    public void sendMessage(String userId, Object message) {
        Session session = sessionPool.get(userId);
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给所有用户发送消息
     *
     * @param message
     */
    public void broadCast(Object message) {
        for (Session session : sessionPool.values()) {
            try {
                sendMessage(session, message);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    // 建立连接成功调用
    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam(value = "connect_message") String connectMessage)
            throws EncodeException, IOException {
        // ws环境无法通过该方法获取登录信息
        String userId = connectMessage;
        if (StringUtils.isNotBlank(userId) && !loginUsers.containsKey(userId)) {
            sessionPool.put(userId, session);
        } else {
            sendMessage(session, "");
        }

        // 1. 连接时获取该用户参与的未完成任务情况
        List<BcpTask> unfinishedTaskList = bcpTaskSrv.getTaskList(userId, true);
        // 不管是否为空都发
        sendMessage(session, unfinishedTaskList);

        // 2. 获取该用户参与的任务的最新计算结果
        if (CollectionUtils.isEmpty(unfinishedTaskList)) {
            // 若没有参与中正在进行的任务则返回空数组
            sendMessage(session, new ArrayList<>());
        } else {
            Set<Long> taskIdSet = unfinishedTaskList.stream().map(BcpTask::getTaskId).collect(Collectors.toSet());
            List<BcpUserModel> resultList = bcpTaskSrv.getDesignatedOrLatestResult(taskIdSet, userId, null, true);
            sendMessage(session, resultList);
        }
    }

    // 关闭连接时调用
    @OnClose
    public void onClose(Session session, @PathParam(value = "connect_message") String connectMessage)
            throws IOException {
        if (session != null) {
            session.close();
            String userId = session.getUserPrincipal().getName();
            sessionPool.remove(userId);
            System.out.println(userId + "断开webSocket连接！当前人数为" + sessionPool.size());
        }
    }

    // 收到客户端信息，视为json格式
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String userId = session.getUserPrincipal().getName();
        ObjectMapper objectMapper = new ObjectMapper();
        // 获取用户发来参数
        BcpUserModel userModel = objectMapper.readValue(message, BcpUserModel.class);
        userModel.setUserId(userId);// websocket环境下设置用户id

        // 提交数据
        bcpTaskSrv.submitBcpTask(userModel);

        System.out.println(userId + "提交数据");
    }

    // 错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    /**
     * 生成一个不可变map返回，用于获取当前登录用户数量 获取时需要同步
     *
     * @return
     */
    public static synchronized Map<String, Session> getSessionPool() {
        Map<String, Session> unmodifiableMap = Collections.unmodifiableMap(sessionPool);
        return unmodifiableMap;
    }

    public static synchronized int getOnlineNum() {
        return sessionPool.size();
    }

    /**
     * 返回在线userId
     *
     * @return
     */
    public static synchronized Set<String> getOnlineUserId() {
        return sessionPool.keySet();
    }

}
