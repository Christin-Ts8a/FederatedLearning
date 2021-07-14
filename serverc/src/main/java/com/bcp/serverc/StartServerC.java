package com.bcp.serverc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = { "com.bcp.serverc.mapper" })
//@EnableSwagger2
// @ComponentScan(value = "com.bcp.serverc")
//@ComponentScan(basePackages = { "com.bcp.serverc" })
public class StartServerC {

	public static void main(String[] args) {
		SpringApplication.run(StartServerC.class, args);
	}
}
