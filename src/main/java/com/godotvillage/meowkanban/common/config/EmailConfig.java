package com.godotvillage.meowkanban.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 *
 * @author mkdir
 * @since 2026/06/22 10:26
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "meow-kanban.email")
public class EmailConfig {
	private String host;
	private Integer port;
	private String from;
	private String fromName;
	private String username;
	private String password;
	private Boolean sslEnabled = true;
	private Boolean tlsEnabled = true;
	private Boolean async = true;
	private Integer timeout = 10000;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		if (port != null) {
			mailSender.setPort(port);
		}
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setDefaultEncoding("UTF-8");

		int effectiveTimeout = timeout != null ? timeout : 10000;
		Properties properties = mailSender.getJavaMailProperties();
		properties.put("mail.smtp.auth", String.valueOf(username != null && !username.isBlank()));
		properties.put("mail.smtp.ssl.enable", String.valueOf(Boolean.TRUE.equals(sslEnabled)));
		properties.put("mail.smtp.starttls.enable", String.valueOf(Boolean.TRUE.equals(tlsEnabled)));
		properties.put("mail.smtp.connectiontimeout", String.valueOf(effectiveTimeout));
		properties.put("mail.smtp.timeout", String.valueOf(effectiveTimeout));
		properties.put("mail.smtp.writetimeout", String.valueOf(effectiveTimeout));
		return mailSender;
	}
}
