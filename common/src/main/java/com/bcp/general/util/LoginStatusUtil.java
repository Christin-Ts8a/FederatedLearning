package com.bcp.general.util;

import org.apache.catalina.Session;
import org.apache.catalina.User;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LoginStatusUtil {
    public static User getUserInfo(HttpServletRequest request) {
//        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
//        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        HttpSession session = request.getSession();
       return (User)session.getAttribute("SYSTEM_USER_SESSION");
    }
}
