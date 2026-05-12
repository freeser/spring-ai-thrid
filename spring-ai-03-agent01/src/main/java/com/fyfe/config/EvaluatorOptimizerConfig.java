package com.fyfe.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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

    @Bean
    public ReactAgent writerChatAgent() {
        return ReactAgent.builder()
                .name("writer")
                .model(dashScopeChatModel)
                .instruction("你是一位经验丰富的科技文案写作者，完成客户的需求：{input}")
                .outputKey("content")
                .build();
    }

    @Bean
    public ReactAgent qualityEvaluatorChatAgent() {
        return ReactAgent.builder()
                .name("qualityEvaluator")
                .model(dashScopeChatModel)
                .instruction("你是十分严格的文案评估师，熟知各类文案写作规范与技巧，" +
                        "能精准评判文案内容的逻辑性、创新性以及语言表达的流畅性，" +
                        "确认通过在最后输出'WORK_SUCCESS'，" +
                        "不通过在最后输出'WORK_FAIL'，" +
                        "最少需要一次修正，" +
                        "作为评估师只需要提出建议不需要修改文稿" +
                        "下面是写作的文案：{content}")
                .outputKey("result")
                .build();
    }

    // 编写工作流
    // StateGraph 工作流对象
    @Bean
    public StateGraph evaluationOptimizerWorkflow(
            ReactAgent writerChatAgent,
            ReactAgent qualityEvaluatorChatAgent
    ) throws GraphStateException {
        // 1、配置工作流状态管理策略
        // 定义状态的更新规则：控制工作流中数据的覆盖以及追加逻辑
        // 定义状态管理策略
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> strategies = new HashMap<>();
            strategies.put("input", new ReplaceStrategy());
            return strategies;
        };
        // 创建状态图工作流实例
        // 构建包含 Agent 的工作流
        StateGraph workflow = new StateGraph(keyStrategyFactory);
        // 将 Agent 作为 SubGraph Node 添加
        // 将agent注册到工作流上
        workflow.addNode(
            writerChatAgent.name(),
            writerChatAgent.asNode(
                true,     // includeContents: 是否传递父图的消息历史，包含上下文，让agent获得上下文
                true                    // returnReasoningContents: 是否返回推理过程
            )
        );

        workflow.addNode(
                qualityEvaluatorChatAgent.name(),
                qualityEvaluatorChatAgent.asNode(true, true)
        );


        // 定义执行流程
        // 定义固定流程
        workflow.addEdge(StateGraph.START, writerChatAgent.name()); // 指定开始流程 -- 写作为开始
        workflow.addEdge(writerChatAgent.name(), qualityEvaluatorChatAgent.name()); // 工作固定流程，写作 评估

        // 定义分支流程【逻辑】
        workflow.addConditionalEdges(
                qualityEvaluatorChatAgent.name(),
                AsyncEdgeAction.edge_async(state -> {
                    AssistantMessage resultMessage = (AssistantMessage) state.data().get("result");
                    String result = resultMessage.getText();
                    System.out.println("result = " + result);
                    if (result.contains("WORK_SUCCESS")) {
                        System.out.println("通过");
                        return "通过";
                    } else {
                        // 未通过的时候，使用评估
                        System.out.println("未通过");
                        return "修订";
                    }
                }),
                Map.of(
                        "通过", StateGraph.END,
                        "修订", writerChatAgent.name()
                )
        );
        return workflow;
    }
}
