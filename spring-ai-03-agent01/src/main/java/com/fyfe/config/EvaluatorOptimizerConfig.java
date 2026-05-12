package com.fyfe.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EvaluatorOptimizerConfig {
    private final DashScopeChatModel dashScopeChatModel;

    // 编写2个客户端
    // 一个用于生成文章
    @Bean("writeClient")
    public ChatClient writerChat(){
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你是一位经验丰富的科技文案写作者，擅长用简洁易懂的语言阐述前沿科技概念")
                .build();
    }
    // 一个用于评估文章
    @Bean("evaClient")
    public ChatClient qualityEvaluatorChat(){
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你是专业的文案评估师，熟知各类文案写作规范与技巧，能精准评判文案内容的逻辑性、创新性以及语言表达的流畅性")
                .build();
    }
}
