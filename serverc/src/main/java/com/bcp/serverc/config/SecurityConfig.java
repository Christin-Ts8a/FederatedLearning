package com.bcp.serverc.config;

import com.bcp.serverc.constant.SecurityConstant;
import com.bcp.serverc.controller.WebSocketServer;
import com.bcp.serverc.filter.RestAuthenticationFilter;
import com.bcp.serverc.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final ObjectMapper objectMapper;

	private final WebSocketServer webSocketServer;

	@Autowired
	UserServiceImpl userService;

	/**
	 * 这难道是对BCryptPasswordEncoder的依赖注入的配置？
	 *
	 * @return
	 */
	 @Bean
	 PasswordEncoder passwordEncoder() {
	 return new BCryptPasswordEncoder();
	 }

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 去除权限版
		http
				// 授权配置
				.authorizeRequests()
				// 无需权限访问
				.antMatchers(SecurityConstant.AUTH_WHITELIST).permitAll()
				// 其他接口需要登录后才能访问
				.anyRequest().authenticated().and()
				.addFilterAt(getRestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.formLogin().disable()
				.httpBasic().disable()
				.csrf().disable().headers().frameOptions().disable().and();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				// 用户认证处理
				.userDetailsService(userService)
				// 密码处理
				.passwordEncoder(bCryptPasswordEncoder);
	}

	private AuthenticationFailureHandler getAuthenticationFailureHandler() {
		return (httpServletRequest, httpServletResponse, e) -> {
			val objectMapper = new ObjectMapper();
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			httpServletResponse.setCharacterEncoding("UTF-8");
			val errData = Map.of(
					"title", "认证失败",
					"details", e.getMessage()
			);
			httpServletResponse.getWriter().println(objectMapper.writeValueAsString(errData));
		};
	}

	private RestAuthenticationFilter getRestAuthenticationFilter() throws Exception {
		RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
		filter.setAuthenticationSuccessHandler(getAuthenticationSuccessHandler());
		filter.setAuthenticationFailureHandler(getAuthenticationFailureHandler());
		filter.setAuthenticationManager(authenticationManager());
		filter.setFilterProcessesUrl("/authorize/login");
		return filter;
	}

	private AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
		return (httpServletRequest, httpServletResponse, authentication) -> {
			WebSocketServer.loginUsers.put(authentication.getName(), httpServletRequest.getSession());
			ObjectMapper objectMapper = new ObjectMapper();
			httpServletResponse.setStatus(HttpStatus.OK.value());
			httpServletResponse.getWriter().println(objectMapper.writeValueAsString(authentication));
		};
	}

	@Override
	public void configure(WebSecurity webSecurity){
		webSecurity.ignoring().antMatchers(
				"/webSocket/**"
		);
	}
}
