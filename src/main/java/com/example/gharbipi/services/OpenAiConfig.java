package com.example.gharbipi.services;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAiService openAiService() {
        String apiKey = "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";
        return new OpenAiService(apiKey);  // Initialize OpenAiService with your API key
    }
}
