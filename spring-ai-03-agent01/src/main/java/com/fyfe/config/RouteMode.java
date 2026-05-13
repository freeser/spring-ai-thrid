package com.fyfe.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.LlmRoutingAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RouteMode {
    private final DashScopeChatModel dashScopeChatModel;

    @Bean
    public ReactAgent writerAgent() {
        return ReactAgent.builder()
                .name("writer_agent")
                .model(dashScopeChatModel)
                .description("擅长创作各类文章，包括散文、诗歌等文学作品")
                .instruction("你是一个知名的作家，擅长写作和创作。请根据用户的提问进行回答。")
                .outputKey("writer_output")
                .build();
    }

    @Bean
    public ReactAgent reviewerAgent() {
        return ReactAgent.builder()
                .name("reviewer_agent")
                .model(dashScopeChatModel)
                .description("擅长对文章进行评论、修改和润色")
                .instruction("你是一个知名的评论家，擅长对文章进行评论和修改。" +
                        "对于散文类文章，请确保文章中必须包含对于西湖风景的描述。")
                .outputKey("reviewer_output")
                .build();
    }

    @Bean
    public ReactAgent translatorAgent() {
        return ReactAgent.builder()
                .name("translator_agent")
                .model(dashScopeChatModel)
                .description("擅长将文章翻译成各种语言")
                .instruction("你是一个专业的翻译家，能够准确地将文章翻译成目标语言。")
                .outputKey("translator_output")
                .build();
    }

    @Bean
    public LlmRoutingAgent routingAgent(ReactAgent writerAgent, ReactAgent reviewerAgent, ReactAgent translatorAgent){
        return LlmRoutingAgent.builder()
                .name("content_routing_agent")
                .description("根据用户需求智能路由到合适的专家Agent")
                .model(dashScopeChatModel)
                .subAgents(List.of(writerAgent, reviewerAgent, translatorAgent))
                .build();
    }
}