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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest(classes = StartApp.class)
public class MyPictureTest {
    @Test
    public void test01() {
        // 文生图
        // 提示词
//         String prompt = "一副典雅庄重的对联悬挂于厅堂之中，房间是个安静古典的中式布置，桌子上放着一些青花瓷，对联上左书“义本生知人机同道善思新”，右书“通云赋智乾坤启数高志远”， 横批“智启千问”，字体飘逸，在中间挂着一幅中国风的画作，内容是岳阳楼。";
        String prompt = "一位身着古装的美女，站在一座桥上。向远处望去，桥下有一群鸭子经过";
        // 自定义参数列表
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prompt_extend", true); // 开启提示词拓展【优化】-- AI按照理解 对提示词进一步的优化
        parameters.put("watermark", false);    // 关闭水映
        parameters.put("negative_prompt", " "); // 负面提示词（不希望生成的内容）

        // 构建图像生成的请求参数对象
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey("sk-8de8b8e9f68048b18bf58c582b0cd2cc")
                        // 当前仅qwen-image-plus、qwen-image模型支持异步接口
                        .model("qwen-image-plus")
                        .prompt(prompt)
                        .n(1) // 生成图片的张数
                        .size("1664*928") // 分辨率尺寸
                        .parameters(parameters) // 扩展参数
                        .build();

        // 图片AI的客户端
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---同步调用，请等待任务执行----");
            result = imageSynthesis.call(param); //对图像大模型进行调用
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result)); //将结果转为JSON打印
    }
    @Test
    public void test02() {
        // 1. 创建多模态对话客户端（核心API调用工具）
        MultiModalConversation conv = new MultiModalConversation();

        // 2. 构建用户消息：传入【2张参考图片 + 1段编辑指令文本】
        // 模型支持输入 1-3 张图片
        MultiModalMessage userMessage = MultiModalMessage.builder()
                .role(Role.USER.getValue())  // 指定角色为用户（固定写法）
                .content(Arrays.asList(
                        // 第一张图片：城市底图（用作编辑背景）
                        Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260310/rdsgaa/image+%2815%29.png"),
                        // 第二张图片：卡通形象参考图（AI参考该风格生成）
                        Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260310/qokhtl/image+%2816%29.png"),
                        // 文字编辑指令：告诉AI如何修改图片
                        Collections.singletonMap("text", "使用图一的城市照片作为底图。请勿更改照片中的真实建筑、街道、车辆或人物。保持照片的真实性。三个图二中的卡通形象在建筑物周围，一个趴在建筑物上方，一个从建筑物的右边探出头来，一个坐在建筑物前的空地上。该形象应采用扁平化的图形风格绘制，轮廓清晰，类似于壁画或海报插图。")
                ))
                .build();

        // 3. 配置生成扩展参数
        // qwen-image-2.0系列支持输出 1-6 张图片
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("watermark", false);      // 关闭水印
        parameters.put("negative_prompt", " ");  // 负面提示词（不希望生成的内容）
        parameters.put("n", 1);                  // 生成图片数量：1张
        parameters.put("prompt_extend", true);   // 开启AI自动优化提示词
        parameters.put("size", "2048*2048");     // 生成图片尺寸

        // 4. 构建多模态对话请求参数（核心配置）
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey("sk-8de8b8e9f68048b18bf58c582b0cd2cc")
                .model("qwen-image-2.0-pro")     // 指定图像编辑模型
                .messages(Collections.singletonList(userMessage)) // 传入用户消息
                .parameters(parameters)          // 传入扩展参数
                .build();

        MultiModalConversationResult result = null;
        try {
            // 5. 同步调用AI图像编辑接口（核心执行方法）
            result = conv.call(param);
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            throw new RuntimeException(e.getMessage());
        }

        // 6. 解析并提取AI返回的【编辑后图片URL】
        List<Map<String, Object>> contentList = result.getOutput().getChoices().get(0).getMessage().getContent();
        int imageIndex = 1;
        for (Map<String, Object> content : contentList) {
            // 遍历结果，只打印image类型的内容（即生成的图片链接）
            if (content.containsKey("image")) {
                System.out.println("输出图像" + imageIndex + "的URL：" + content.get("image"));
                imageIndex++;
            }
        }
    }
}
