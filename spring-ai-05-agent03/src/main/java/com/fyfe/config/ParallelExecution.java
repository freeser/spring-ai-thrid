package com.fyfe.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.ParallelAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
@RequiredArgsConstructor
public class ParallelExecution {
    private final DashScopeChatModel dashScopeChatModel;

    @Bean
    public ReactAgent proseWriterAgent() {
        return ReactAgent.builder()
                .name("prose_writer_agent")
                .model(dashScopeChatModel)
                .description("专门写散文的AI助手")
                .instruction("你是一个知名的散文作家，擅长写优美的散文。" +
                        "用户会给你一个主题：{input}，你只需要创作一篇100字左右的散文。")
                .outputKey("prose_result")
                .build();
    }

    @Bean
    public ReactAgent poemWriterAgent() {
        return ReactAgent.builder()
                .name("poem_writer_agent")
                .model(dashScopeChatModel)
                .description("专门写现代诗的AI助手")
                .instruction("你是一个知名的现代诗人，擅长写现代诗。" +
                        "用户会给你的主题是：{input}，你只需要创作一首现代诗。")
                .outputKey("poem_result")
                .build();
    }

    @Bean
    public ReactAgent summaryAgent() {
        return ReactAgent.builder()
                .name("summary_agent")
                .model(dashScopeChatModel)
                .description("专门做内容总结的AI助手")
                .instruction("你是一个专业的内容分析师，擅长对主题进行总结和提炼。" +
                        "用户会给你一个主题：{input}，你只需要对这个主题进行简要总结。")
                .outputKey("summary_result")
                .build();
    }

    // 创建并行Agent
    @Bean
    public ParallelAgent parallelAgent(ReactAgent proseWriterAgent, ReactAgent poemWriterAgent, ReactAgent summaryAgent) {
        return ParallelAgent.builder()
                .name("parallel_creative_agent")
                .description("并行执行多个创作任务，包括写散文、写诗和做总结")
                .mergeOutputKey("merged_results")
                .subAgents(List.of(proseWriterAgent, poemWriterAgent, summaryAgent))
                .mergeStrategy(new ParallelAgent.DefaultMergeStrategy())
                .build();
    }
}