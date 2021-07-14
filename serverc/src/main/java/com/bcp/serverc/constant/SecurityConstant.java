package com.bcp.serverc.constant;

public interface SecurityConstant {

	/**
	 * 白名单
	 */
	String[] AUTH_WHITELIST = {
			// -- swagger ui
			"/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**",
			// 登录
			"/user/**",
			// 资源
			"/css/**", "/error404", };

}
