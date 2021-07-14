package com.bcp.serverc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:serverProperties.properties")
public class ServerProperties {

	@Value("${sHost}")
	private String sHost;
	
	@Value("${sPort}")
	private String sPort;

	public String getsHost() {
		return sHost;
	}

	public void setsHost(String sHost) {
		this.sHost = sHost;
	}

	public String getsPort() {
		return sPort;
	}

	public void setsPort(String sPort) {
		this.sPort = sPort;
	}
	
}
