package com.fyfe;


import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.flow.agent.ParallelAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@SpringBootTest(classes = StartApp.class)
public class MyTest {
    @Autowired
    private ParallelAgent parallelAgent;

    @Test
    public void test01() throws GraphRunnerException {
        // 使用
        Optional<OverAllState> result = parallelAgent.invoke("以'西湖'为主题");

        if (result.isPresent()) {
            OverAllState state = result.get();

            // 访问各个Agent的输出
            state.value("prose_result").ifPresent(r ->
                    System.out.println("散文: " + r));
            state.value("poem_result").ifPresent(r ->
                    System.out.println("诗歌: " + r));
            state.value("summary_result").ifPresent(r ->
                    System.out.println("总结: " + r));

            // 访问合并后的结果
            state.value("merged_results").ifPresent(r ->
                    System.out.println("合并结果: " + r));
        }
    }
}
