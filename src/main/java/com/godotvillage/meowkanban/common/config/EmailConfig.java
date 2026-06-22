package com.godotvillage.meowkanban.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
}
