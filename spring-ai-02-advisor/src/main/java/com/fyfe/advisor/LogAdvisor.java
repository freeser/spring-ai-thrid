package com.fyfe.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

public class LogAdvisor implements BaseAdvisor {
    private static final Logger log = LoggerFactory.getLogger(LogAdvisor.class);

    /**
     * 请求前处理：记录用户输入和使用的模型
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 1. 日志：记录请求的提示词和模型
        log.info("【请求日志】用户输入: {}, 使用模型: {}",
                chatClientRequest.prompt().getUserMessage().getText(),
                chatClientRequest.prompt().getOptions().getModel());

        return chatClientRequest;
    }

    /**
     * 请求后处理：记录AI的回复内容
     */
    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        // 1. 日志：记录 AI 的回复内容
        log.info("【响应日志】AI 回复: {}", chatClientResponse.chatResponse().getResult().getOutput().getText());
        return chatClientResponse;
    }

    /**
     * 指定Advisor的执行优先级，返回的值为非负整数，值越小优先级越高
     */
    @Override
    public int getOrder() {
        return 2;
    }
}