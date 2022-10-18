package com.bcp.serverc.interceptor;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.mapper.UserMapper;
import com.bcp.serverc.model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
@Aspect
public class LoginInterceptor {
    public static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Resource
    private UserMapper userMapper;

    @Around("execution(* com.bcp.serverc.controller.*.*(..)) && !execution(* com.bcp.serverc.controller.WebSocketServer.*(..))")
    public Object loginInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("--------------------开始执行{}.{}--------------------",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        long startTime = System.currentTimeMillis();

        // 获取 session 中的用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        Object requestSessionInfo = request.getSession().getAttribute("SYSTEM_USER_SESSION");
        Object userSessionInfo = session.getAttribute("SYSTEM_USER_SESSION");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println(request.getRequestURL());
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " " +cookie.getValue());
                System.out.println(cookie.getMaxAge() + " " +cookie.getDomain());
                System.out.println(cookie.getPath());
                if ("username".equals(cookie.getName())
                        && cookie.getMaxAge() > 0
                        && userSessionInfo == null) {
                    User user = new User();
                    user.setUsername(cookie.getValue());
                    User loginUser = userMapper.selectOne(user);
                    session.setAttribute("SYSTEM_USER_SESSION", loginUser);
                }
            }
        }
        String methodName = joinPoint.getSignature().getName();
        if (userSessionInfo == null
                && !"login".equals(methodName)
                && !"logout".equals(methodName)
                && !"userExist".equals(methodName)) {
            return JsonResult.errorMsg("用户未登录");
        }

        // 执行对应请求
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long takeTime = endTime - startTime;
        if (takeTime > 3000) {
            logger.error("--------------------执行结束- 用时{} ms--------------------", takeTime);
        } else if (takeTime > 2000) {
            logger.warn("--------------------执行结束- 用时{} ms--------------------", takeTime);
        } else {
            logger.info("--------------------执行结束- 用时{} ms--------------------", takeTime);
        }
        return result;
    }
}
