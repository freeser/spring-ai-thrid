package com.fyfe.service;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.fyfe.form.MyPictureForm;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestService {

    @Resource
    @Qualifier(value = "picparam")
    private Map<String, Object> params;

    @Resource
    private MultiModalConversation multiModalConversation;

    public List<String> getPicture(MyPictureForm form) {
        MultiModalMessage userMessage = MultiModalMessage.builder()
                .role(Role.USER.getValue())  // 指定角色为用户（固定写法）
                .content(Arrays.asList(
                        // 第一张图片：城市底图（用作编辑背景）
                        Collections.singletonMap("image", form.getImages().get(0)),
                        // 第二张图片：卡通形象参考图（AI参考该风格生成）
                        Collections.singletonMap("image",form.getImages().get(1)),
                        // 文字编辑指令：告诉AI如何修改图片
                        Collections.singletonMap("text",form.getText())
                ))
                .build();

        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey("sk-8de8b8e9f68048b18bf58c582b0cd2cc")
                .model("qwen-image-2.0-pro")     // 指定图像编辑模型
                .messages(Collections.singletonList(userMessage)) // 传入用户消息
                .parameters(params)          // 传入扩展参数
                .build();

        MultiModalConversationResult result = null;
        try {
            // 5. 同步调用AI图像编辑接口（核心执行方法）
            result = this.multiModalConversation.call(param);
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            throw new RuntimeException(e.getMessage());
        }

        // 6. 解析并提取AI返回的【编辑后图片URL】
        List<Map<String, Object>> contentList = result.getOutput().getChoices().get(0).getMessage().getContent();
        int imageIndex = 1;
        ArrayList<String> returnResult = new ArrayList<String>();
        for (Map<String, Object> content : contentList) {
            // 遍历结果，只打印image类型的内容（即生成的图片链接）
            if (content.containsKey("image")) {
                System.out.println("输出图像" + imageIndex + "的URL：" + content.get("image"));
                returnResult.add(content.get("image").toString());
                imageIndex++;
            }
        }
        return  returnResult;
    }
}
