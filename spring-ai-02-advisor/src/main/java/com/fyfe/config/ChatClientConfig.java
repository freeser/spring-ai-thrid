package com.fyfe.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.fyfe.advisor.FilterAdvisor;
import com.fyfe.advisor.LogAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    @Resource
    private DashScopeChatModel dashScopeChatModel;

    @Bean
    public ChatClient getChatClient() {
        return ChatClient.builder(dashScopeChatModel).defaultAdvisors(
                new LogAdvisor(),
                new FilterAdvisor()
        ).build();
    }
}
