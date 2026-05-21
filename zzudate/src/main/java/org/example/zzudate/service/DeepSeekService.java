package org.example.zzudate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DeepSeekService {

   private static final Logger log = LoggerFactory.getLogger(DeepSeekService.class);

   @Value("${deepseek.api-key}")
   private String apiKey;
   private static final String API_URL="https://api.deepseek.com/v1/chat/completions";
   private final HttpClient httpClient=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();

   @Async
   public CompletableFuture<String> generateMatchCommentAsync(String userAInfo, String userBInfo, double matchScore) {
      try {
         String prompt="你是一位温柔有趣的郑州大学（ZZU）校园月老AI。\n请根据以下两位同学的灵魂问卷答案，为他们生成一段温暖的匹配评语。\n\n=== 用户A的答案 ===\n%s\n\n=== 用户B的答案 ===\n%s\n\n匹配得分：%.0f分（满分100分）\n\n要求：\n1. 仔细阅读两人的答案，找出他们在性格、价值观、生活习惯上的契合点，具体提及1-2个共同的答案细节。\n2. 结合郑州大学校园元素（如：樱花园散步、眉湖吹风、厚山看日落、核心教学区自习、食堂一起吃饭等）给出具体的第一次约会场景建议。\n3. 语气要温暖、真诚、带一点校园浪漫感，不要太官方。\n4. 纯文本输出，不要用Markdown，控制在150字左右。\n5. 直接输出评语内容，不要加\"AI月老已为您生成专属评语\"这类前缀。\n".formatted(userAInfo, userBInfo, matchScore);
         JSONObject requestBody=new JSONObject();
         requestBody.put("model", "deepseek-chat");
         requestBody.put("temperature", 0.7);
         JSONArray messages = new JSONArray();
         JSONObject message = new JSONObject();
         message.put("role", "user");
         message.put("content", prompt);
         messages.add(message);
         requestBody.put("messages", messages);
         HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.deepseek.com/v1/chat/completions")).timeout(Duration.ofSeconds(30L)).header("Content-Type", "application/json").header("Authorization", "Bearer " + this.apiKey).POST(BodyPublishers.ofString(requestBody.toJSONString())).build();
         HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
         if (response.statusCode() == 200) {
            JSONObject jsonResponse = JSON.parseObject((String)response.body());
            String comment = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            return CompletableFuture.completedFuture(comment.trim());
         } else {
            log.error("DeepSeek API 错误: " + (String)response.body());
            return CompletableFuture.completedFuture("月老暂时打了个盹，请稍后再试哦~");
         }
      } catch (Exception e) {
         log.error("DeepSeek API 调用失败", e);
         return CompletableFuture.completedFuture("校园网好像被风吹断了，评语生成失败，请稍后重试~");
      }
   }
}
