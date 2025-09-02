package com.contest.sports_programming_server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "secure")
@Configuration
@Data
public class SecureProperties {
    private String username;
    private String password;
}
