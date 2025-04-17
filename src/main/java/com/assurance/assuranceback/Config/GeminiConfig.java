package com.assurance.assuranceback.Config;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
@Slf4j
public class GeminiConfig {

    private final String API_KEY = "AIzaSyAVbLIFs5L8fmtHnAi4ye1aH97yVCEE-74";

    @Bean
    public WebClient webClient() {
        // Utiliser l'URL exacte qui a fonctionné dans Postman
        return WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + API_KEY)
                .build();
    }

    public String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getCurrentUser() {
        return "Hosinusss";
    }
}