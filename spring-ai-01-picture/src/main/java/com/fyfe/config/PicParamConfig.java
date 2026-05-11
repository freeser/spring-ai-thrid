package com.fyfe.config;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class PicParamConfig {
    @Value("${picture.param.watermark}")
    private String wateRmark;

    @Value("${picture.param.negative_prompt}")
    private String negativePrompt;

    @Value("${picture.param.size}")
    private String size;

    @Value("${picture.param.num}")
    private String num;

    @Value("${picture.param.prompt_extend}")
    private String promptExtend;

    @Bean(name = "picparam")
    public Map<String, Object> getParams() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("watermark", this.wateRmark);      // 关闭水印
        parameters.put("negative_prompt", this.negativePrompt.isEmpty() ? " " : this.negativePrompt);  // 负面提示词（不希望生成的内容）
        parameters.put("n", this.num);                  // 生成图片数量：1张
        parameters.put("prompt_extend", this.promptExtend);   // 开启AI自动优化提示词
        parameters.put("size", this.size);     // 生成图片尺寸
        return parameters;
    }

    @Bean
    public MultiModalConversation getMultiModalConversation() {
        return new MultiModalConversation();
    }

//    public MultiModalConversationParam getMultiModalConversationParam(HashMap<String, Object> params) {
//        MultiModalConversationParam param = MultiModalConversationParam.builder()
//                .apiKey("sk-8de8b8e9f68048b18bf58c582b0cd2cc")
//                .model("qwen-image-2.0-pro")     // 指定图像编辑模型
//                .messages(Collections.singletonList(userMessage)) // 传入用户消息
//                .parameters(parameters)          // 传入扩展参数
//                .build();
//    }
}
