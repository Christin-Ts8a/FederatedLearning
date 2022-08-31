package com.bcp.serverc.controller;

import com.bcp.general.model.BcpUserModel;
import com.bcp.serverc.interceptor.LoginInterceptor;
import com.bcp.serverc.service.impl.BcpTaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
//@ServerEndpoint(value = "/webSocket/{connect_message}")
@ServerEndpoint(value = "/webSocket")
public class WebSocketServer {

    public static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static volatile ConcurrentHashMap<String, String> sessionPool = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String, String> loginOrg = new ConcurrentHashMap<>();

    @Autowired
    private static BcpTaskServiceImpl bcpTaskSrv;

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
     * @param sessionId
     * @param message
     */
    public void sendMessage(String sessionId, Object message) {
        sendMessage(sessionPool.get(sessionId), message);
    }

    /**
     * 给指定用户发送信息
     *
     * @param orgCode
     * @param message
     */
    public void sendMessageByOrgCode(String orgCode, Object message) {
        String address = loginOrg.get(orgCode);
        for (Map.Entry<String, String> entry :
                sessionPool.entrySet()) {
            if (entry.getValue().equals(address)) {
                sendMessage(entry.getKey(), message);
            }
        }
    }

    /**
     * 给所有用户发送消息
     *
     * @param message
     */
    public void broadCast(Object message) {
        sessionPool.entrySet().forEach(entry -> {
            try {
                sendMessage(entry.getKey(), message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 建立连接成功调用
    @OnOpen
//    public void onOpen(Session session, @PathParam(value = "connect_message") String username)
    public void onOpen(Session session)
            throws EncodeException, IOException {
        InetSocketAddress remoteAddress = getRemoteAddress(session);
        if (!loginOrg.contains(remoteAddress.getHostName())) {
            logger.error(remoteAddress.getHostName() + "未授权登录");
            sendMessage(session, "未授权登录");
            session.close();
        } else {
            logger.info(remoteAddress.getHostName() + "登录成功");
            sessionPool.put(session.getId(), remoteAddress.getHostName());
            sendMessage(session, "登录成功");
        }

        // 1. 连接时获取该用户参与的未完成任务情况
//        List<BcpTask> unfinishedTaskList = bcpTaskSrv.getTaskList(username, true);
        // 不管是否为空都发
//        sendMessage(session, unfinishedTaskList);

        // 2. 获取该用户参与的任务的最新计算结果
//        if (CollectionUtils.isEmpty(unfinishedTaskList)) {
            // 若没有参与中正在进行的任务则返回空数组
//            sendMessage(session, new ArrayList<>());
//        } else {
//            Set<Long> taskIdSet = unfinishedTaskList.stream().map(BcpTask::getTaskId).collect(Collectors.toSet());
//            List<BcpUserModel> resultList = bcpTaskSrv.getDesignatedOrLatestResult(taskIdSet, username, null, true);
//            sendMessage(session, resultList);
//        }
    }

    // 关闭连接时调用
    @OnClose
    public void onClose(Session session)
            throws IOException {
        InetSocketAddress remoteAddress = getRemoteAddress(session);
        if (session != null) {
            String address = sessionPool.get(session.getId());
            for (Map.Entry<String, String> entry : loginOrg.entrySet()) {
                if (entry.getValue().equals(address)) {
                    loginOrg.remove(entry.getKey());
                }
            }
            session.close();
            sessionPool.remove(session.getId());
            logger.info(remoteAddress.getHostName() + "断开连接, 当前连接人数: " + sessionPool.size());
        }
    }

    // 收到客户端信息，视为json格式
    @OnMessage
    public void onMessage(Session session, String message) throws IOException, EncodeException {
        logger.info("receive message: " +  message);
        if (sessionPool.get(session.getId()) == null) {
            Map<String, String> result = new HashMap();
            result.put("status", "-1");
            result.put("message", "用户未登录");
            result.put("data", null);
            sendMessage(session, result);
            session.close();
        }

        String orgAddress = sessionPool.get(session.getId());
        String loginOrgCode = "";
        for(Map.Entry<String, String> entry : loginOrg.entrySet()) {
            if (entry.getValue().equals(orgAddress)) {
                loginOrgCode = entry.getKey();
            }
        }
        if (StringUtils.isBlank(loginOrgCode)) {
            Map<String, String> result = new HashMap();
            result.put("status", "-1");
            result.put("message", "用户未登录");
            result.put("data", null);
            sendMessage(session, result);
            session.close();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // 获取用户发来参数
        BcpUserModel userModel = objectMapper.readValue(message, BcpUserModel.class);
        userModel.setOrgCode(loginOrgCode);// websocket环境下设置用户id

        logger.info("receive data from " + getRemoteAddress(session).getHostName() + ": " + userModel.toString());

        // 提交数据
        bcpTaskSrv.submitBcpTask(userModel);
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
    public static synchronized Map<String, String> getSessionPool() {
        Map<String, String> unmodifiableMap = Collections.unmodifiableMap(sessionPool);
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

    public static InetSocketAddress getRemoteAddress(Session session) {
        if (session == null) {
            return null;
        }
        RemoteEndpoint.Async async = session.getAsyncRemote();

        //在Tomcat 8.0.x版本有效
//		InetSocketAddress addr = (InetSocketAddress) getFieldInstance(async,"base#sos#socketWrapper#socket#sc#remoteAddress");
        //在Tomcat 8.5以上版本有效
        InetSocketAddress addr = (InetSocketAddress) getFieldInstance(async,"base#socketWrapper#socket#sc#remoteAddress");
        return addr;
    }

    private static Object getFieldInstance(Object obj, String fieldPath) {
        String fields[] = fieldPath.split("#");
        for (String field : fields) {
            obj = getField(obj, obj.getClass(), field);
            if (obj == null) {
                return null;
            }
        }

        return obj;
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field;
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
            }
        }

        return null;
    }
}
