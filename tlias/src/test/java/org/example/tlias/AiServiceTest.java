package org.example.tlias;

import org.example.tlias.ai.AiService;
import org.example.tlias.ai.AiServiceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class AiServiceTest {

    @Resource
    private AiServiceFactory aiServiceFactory;

    @Test
    public void testAiChat() throws InterruptedException {
        // 创建 AI 服务实例
        AiService aiService = aiServiceFactory.createAiService(1L);

        // 准备测试消息
        String message = "你好，徒步专家，我想了解一些关于徒步旅行的建议。";

        // 调用 AI 服务进行对话（流式）
        Flux<String> responseFlux = aiService.chat(message);

        // 使用 CountDownLatch 等待异步操作完成
        CountDownLatch latch = new CountDownLatch(1);

        // 订阅响应流
        responseFlux.subscribe(
                chunk -> {
                    // 打印每个响应块
                    System.out.print(chunk);
                },
                error -> {
                    // 处理错误
                    System.err.println("AI 对话出错: " + error.getMessage());
                    latch.countDown();
                },
                () -> {
                    // 完成时的操作
                    System.out.println("\nAI 对话完成");
                    latch.countDown();
                }
        );

        // 等待异步操作完成，最多等待 30 秒
        latch.await(30, TimeUnit.SECONDS);
    }
}