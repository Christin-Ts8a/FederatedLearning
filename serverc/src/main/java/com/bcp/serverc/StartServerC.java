package com.bcp.serverc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = { "com.bcp.serverc.mapper" })
//@EnableSwagger2
// @ComponentScan(value = "com.bcp.serverc")
//@ComponentScan(basePackages = { "com.bcp.serverc" })
public class StartServerC extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(StartServerC.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(StartServerC.class);
	}
}
