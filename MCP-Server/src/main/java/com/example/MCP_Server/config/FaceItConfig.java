package com.example.MCP_Server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class FaceItConfig {

    @Value("${faceit.api.key:${FACEIT_API_KEY:test_key}}")
    private String apiKey;

    @Bean
    public WebClient faceItWebClient() {
        return WebClient.builder()
                .baseUrl("https://open.faceit.com/data/v4")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}