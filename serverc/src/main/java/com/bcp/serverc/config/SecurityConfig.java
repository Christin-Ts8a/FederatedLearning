package com.bcp.serverc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bcp.serverc.constant.SecurityConstant;
import com.bcp.serverc.service.impl.UserServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
		http
				// 关闭csrf防护
				.csrf().disable().headers().frameOptions().disable().and();
		http
//				.httpBasic().and();
				// 登录处理
				.formLogin().defaultSuccessUrl("/index.html") // 表单方式，没有.loginPage时使用security默认表单
//				.loginPage("/login.html").loginProcessingUrl("/login").defaultSuccessUrl("/index")
				// 成功登陆后跳转页面
//				.failureUrl("/loginError")
				.permitAll().and();
		// 去除权限版
		http.authorizeRequests() // 授权配置
		// 无需权限访问
		.antMatchers(SecurityConstant.AUTH_WHITELIST).permitAll()
		// 其他接口需要登录后才能访问
		.anyRequest().authenticated().and();
		
		http.rememberMe().and();
		
//		 http.authorizeRequests().anyRequest().permitAll().and().logout().permitAll();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				// 用户认证处理
				.userDetailsService(userService)
				// 密码处理
				.passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// 设置拦截忽略文件夹，可以对静态资源放行
		// web.ignoring().antMatchers("/**");
	}

}
