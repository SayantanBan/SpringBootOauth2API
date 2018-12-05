package com.sayan.authentication.OauthService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@PropertySource("application.properties")
public class HttpSessionConfig {
	
	@Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;
    
    @Value("${spring.redis.password}")
    private String password;
    
	@Bean
	public LettuceConnectionFactory connectionFactory() {
		LettuceConnectionFactory factory= new LettuceConnectionFactory();
		factory.setHostName(host);
		factory.setPassword(password);
		factory.setPort(port);
		return factory;
	}
}

