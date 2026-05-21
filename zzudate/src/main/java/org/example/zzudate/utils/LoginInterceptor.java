package org.example.zzudate.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.example.zzudate.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
   private final StringRedisTemplate stringRedisTemplate;
   private final UserService userService;

   public LoginInterceptor(StringRedisTemplate stringRedisTemplate, UserService userService) {
      this.stringRedisTemplate = stringRedisTemplate;
      this.userService = userService;
   }

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
      if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
         return true;
      } else {
         String uri = request.getRequestURI();
         if (!uri.endsWith("/test/aicomment") && !"OPTIONS".equalsIgnoreCase(request.getMethod())) {
            String accessToken = request.getHeader("Authorization");
            if (accessToken == null) {
               response.setStatus(401);
               response.setContentType("application/json;charset=UTF-8");
               response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
               return false;
            } else {
               String key = "login:accessToken:" + accessToken;
               String userId = (String)this.stringRedisTemplate.opsForValue().get(key);
               if (userId == null) {
                  response.setStatus(401);
                  response.setContentType("application/json;charset=UTF-8");
                  response.getWriter().write("{\"code\":401,\"message\":\"登录已过期\"}");
                  return false;
               } else {
                  this.stringRedisTemplate.expire(key, 48L, TimeUnit.HOURS);
                  CurrentUser.setUserId(userId);
                  // 使用 addExpWithDailyCap 防止刷经验
                  // 登录经验每日上限20点（只能获得一次）
                  this.userService.addExpWithDailyCap(userId, 20L, 20L, "login");

                  return true;
               }
            }
         } else {
            return true;
         }
      }
   }

   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
      CurrentUser.remove();
   }
}
