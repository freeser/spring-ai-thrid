package com.fyfe.advisor;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;

public class ModelSwitchAdvisor implements BaseAdvisor {
    /**
     * 请求前处理：动态切换模型
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获取当前的模型选项
        DashScopeChatOptions dashScopeChatOptions = (DashScopeChatOptions) chatClientRequest.prompt().getOptions();
        if (dashScopeChatOptions != null) {
            dashScopeChatOptions.setModel("deepseek-v3.2");
        }

        // 请求处理
        String contents = chatClientRequest.prompt().getContents();
        ChatClientRequest newChatClientRequest = null;
        if (contents.length() > 10) {
            // 使用mutate复制原对象，避免修改原对象
            newChatClientRequest = chatClientRequest
                    .mutate()
                    // 使用builder创建新的Prompt对象
                    .prompt(Prompt.builder()
                            .content(contents)
                            .chatOptions(
                                    dashScopeChatOptions
                            )
                            .build())
                    .build();

        } else {
            newChatClientRequest = chatClientRequest;
        }
        return newChatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}