package com.fyfe;


import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(classes = StartApp.class)
public class MyTest {

    @Autowired
    private ChatClient writeClient; // 写作者
    @Autowired
    private ChatClient evaClient; // 评估者

    @Test
    public void text01() {
        // 构建评估优化器模式的工作流
        String prompt = "请编写一篇关于丰田酷路泽的介绍短文,内容包括发展历史、发展趋势以及优缺点内容,字数控制在500字左右";

        // 初始化变量
        String draft = writeClient.prompt(prompt).call().content();
        String evaluation;
        String optimizedContent = draft;
        boolean isApproved = false;

        // 循环直到评估通过
        while (!isApproved) {
            // 使用质量评估者 ChatClient 对当前内容进行评估
            evaluation = evaClient.prompt("请评估以下文章的质量，如果评估通过则输出pass，否则请提出改进建议并进行改进：" + optimizedContent).call().content();

            // 检查评估结果是否通过（假设评估结果中包含"通过"关键字表示满意）
            if (evaluation.contains("pass")) {
                isApproved = true;
                System.out.println("文章已通过评估！");
            } else {
                // 根据评估建议优化文章
                optimizedContent = writeClient.prompt("请根据以下评估建议优化文章：" + evaluation + " 原始文章：" + optimizedContent).call().content();
                System.out.println("正在进行优化...");
            }
        }

        // 输出最终结果
        System.out.println("优化后的文章：\n" + optimizedContent);
    }

    @Autowired
    private StateGraph evaluationOptimizerWorkflow;

    @Test
    public void text02() throws GraphStateException {
        // 编译执行工作流
        CompiledGraph compiledGraph = evaluationOptimizerWorkflow.compile(
                CompileConfig.builder().build()
        );
        NodeOutput lastOutput = compiledGraph.stream(Map.of("input", "请编写一篇关于丰田酷路泽的介绍短文,内容包括发展历史、发展趋势以及优缺点内容,字数控制在500字左右"))
                .doOnNext(nodeOutput -> {
                    if (nodeOutput instanceof StreamingOutput<?> streamingOutput) {
                        System.out.println("从节点输出：" + streamingOutput.node() + ":"
                                + streamingOutput.agent() + ":"
                                + streamingOutput.message().getText());
                    }
                })
                .blockLast();
        // 由于在评估后面添加的END，所以最后一次输出默认是评估的结果，这里通过data的方式获取到其中的content的结果，也就是作者写的文章
        AssistantMessage content = (AssistantMessage) lastOutput.state().data().get("content");
        System.out.println("最后一次输出：\n" + content.getText());
    }
}
