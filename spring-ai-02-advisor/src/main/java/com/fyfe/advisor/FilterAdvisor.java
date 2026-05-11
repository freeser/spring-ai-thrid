package com.fyfe.advisor;

import cn.hutool.dfa.WordTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

import java.util.List;

public class FilterAdvisor implements BaseAdvisor {
    private static final Logger log = LoggerFactory.getLogger(LogAdvisor.class);
    // 初始化敏感词树（项目启动时加载一次即可）
    private static final WordTree WORD_TREE = new WordTree();

    static {
        // 这里可以改为从数据库或配置中心读取
        List<String> words = List.of("违禁词A", "违禁词B", "敏感词");
        WORD_TREE.addWords(words);
    }

    /**
     * 请求前处理：敏感词过滤
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String userInput = chatClientRequest.prompt().getUserMessage().getText();

        // 2. 敏感词检查 (isMatch 只判断是否包含，match 会找出所有词)
        if(WORD_TREE.isMatch(userInput)){
            // 找出所有敏感词，方便记录日志
            List<String> foundWords = WORD_TREE.matchAll(userInput);
            log.info("【拦截】包含敏感词: {}, 用户输入: {}", foundWords, userInput);
            // 这里也可以直接调用after方法使用固定的语句进行返回
            throw new RuntimeException("消息包含敏感词，请求被拦截");
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
