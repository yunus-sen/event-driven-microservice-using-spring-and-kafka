package com.yunus.sen.mailservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicProperties {
    private String mail;
}
