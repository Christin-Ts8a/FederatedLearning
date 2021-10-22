package com.bcp.serverc.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import com.bcp.serverc.controller.WebSocketServer;
import com.bcp.serverc.service.impl.UserServiceImpl;
import org.apache.catalina.session.StandardSessionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.util.WebAppRootListener;

//@EnableAutoConfiguration
//@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
@Configuration
public class WebSocketConfig extends ServerEndpointConfig.Configurator  implements ServletContextInitializer {

//    /* 修改握手,就是在握手协议建立之前修改其中携带的内容 */
//    @Override
//    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//        HttpSession httpSession = (HttpSession) request.getHttpSession();
//        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
//    }

    /**
     * ServerEndpointExporter 作用
     *
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    //配置websocket传输大小，50M
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","52428800");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","52428800");
    }

    @Autowired
    public void setUp(UserServiceImpl userService) {
        WebSocketServer.userService = userService;
    }


}