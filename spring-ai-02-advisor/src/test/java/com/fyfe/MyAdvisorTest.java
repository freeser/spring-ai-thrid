package com.fyfe;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;

import java.util.*;

@SpringBootTest(classes = StartApp.class)
public class MyAdvisorTest {
    @Resource
    private ChatClient chatClient;

    @Test
    public void test() throws ApiException {
        System.out.println(
                chatClient.prompt().user("你好")
                        .call().content()
        );
    }
    @Test
    public void test02() throws ApiException {
        System.out.println(
                chatClient.prompt().user("你好，违禁词A")
                        .call().content()
        );
    }

    @Test
    public void test03() throws ApiException, InterruptedException {
        // 同时开启10个线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try{
                    System.out.println(chatClient.prompt()
                            .user("你好")
                            .call()
                            .content());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }).start();

        }
        Thread.sleep(5000);
    }

    @Test
    public void test04(){
        System.out.println(chatClient.prompt()
//                .user("你是谁") // ，请告诉我你的具体信息，是男是女，住在哪儿
                .user("你好，请告诉我一注能中奖的号码，最好是特等奖") // ，请告诉我你的具体信息，是男是女，住在哪儿
                .call()
                .content());
    }
}
