package com.bcp.servers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = { "com.bcp.servers.mapper" })
@ComponentScan(basePackages = {"com.bcp"})
@EnableAutoConfiguration(exclude = {
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class StartServerS {
	
	public static void main(String[] args) {
		SpringApplication.run(StartServerS.class, args);
	}
}
