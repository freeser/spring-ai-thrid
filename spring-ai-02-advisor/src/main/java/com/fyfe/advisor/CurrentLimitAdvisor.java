package com.fyfe.advisor;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

public class CurrentLimitAdvisor implements BaseAdvisor {
    private static final Logger log = LoggerFactory.getLogger(CurrentLimitAdvisor.class);

    // 限流：每秒允许2个请求 Guava令牌桶
    // 这里的2.0 代表每秒生成2个令牌
    private final RateLimiter rateLimiter = RateLimiter.create(2.0);

    /**
     * 请求前处理：限流检查
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String userInput = chatClientRequest.prompt().getUserMessage().getText();

        // 限流检查 (tryAcquire 是非阻塞的，拿不到令牌立即返回 false)
        if (!rateLimiter.tryAcquire()) {
            log.warn("【限流】请求被拒绝，用户输入: {}", userInput);
            throw new RuntimeException("系统繁忙，请稍后再试（限流触发）");
        }
        return chatClientRequest;
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
