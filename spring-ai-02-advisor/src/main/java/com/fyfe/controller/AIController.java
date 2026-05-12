package com.fyfe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AIController {

    private final ChatClient chatClient;

    /**
     * AI 对话接口
     * 调用会自动产生 Trace 和 Metrics
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "讲一个关于程序员的笑话") String message) {
        // 这一行调用会自动产生 Trace 和 Metrics
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}