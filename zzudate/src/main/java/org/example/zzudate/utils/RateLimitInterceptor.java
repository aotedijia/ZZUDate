package org.example.zzudate.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> rateLimitScript;

    public RateLimitInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        // 加载Lua脚本
        this.rateLimitScript = RedisScript.of(
                "-- 固定窗口限流脚本\n" +
                "-- KEYS[1]: 限流key\n" +
                "-- ARGV[1]: 限流阈值\n" +
                "-- ARGV[2]: 时间窗口(秒)\n" +
                "\n" +
                "local current = redis.call('INCR', KEYS[1])\n" +
                "if current == 1 then\n" +
                "    redis.call('EXPIRE', KEYS[1], ARGV[2])\n" +
                "end\n" +
                "\n" +
                "if tonumber(current) > tonumber(ARGV[1]) then\n" +
                "    return 0\n" +
                "else\n" +
                "    return 1\n" +
                "end",
                Long.class
        );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 获取客户端IP
        String clientIp = getClientIp(request);
        
        // 1. IP全局限流：每分钟最多15次请求
        String ipKey = "rate:ip:" + clientIp;
        Long ipResult = stringRedisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(ipKey),
                "15",      // 阈值
                "60"       // 60秒
        );
        
        if (ipResult != null && ipResult == 0) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\"}");
            return false;
        }

        // 2. 用户级别限流（需要登录的接口）
        // 注意：此拦截器在LoginInterceptor之前执行，CurrentUser尚未设置
        // 因此需要自行从Redis获取userId
        String accessToken = request.getHeader("Authorization");
        String userId = null;
        if (accessToken != null && !accessToken.isEmpty()) {
            userId = stringRedisTemplate.opsForValue().get("login:accessToken:" + accessToken);
        }
        if (userId == null) {
            return true; // 未登录用户已通过IP限流，直接放行
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 发帖限流：每分钟最多1个帖子
        if ("/Info/saveInfo".equals(uri) && "POST".equalsIgnoreCase(method)) {
            String postKey = "rate:user:" + userId + ":post";
            Long postResult = stringRedisTemplate.execute(
                    rateLimitScript,
                    Collections.singletonList(postKey),
                    "1",       // 阈值：1个帖子
                    "60"       // 60秒
            );
            
            if (postResult != null && postResult == 0) {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":429,\"message\":\"发帖过于频繁，每分钟最多发1个帖子\"}");
                return false;
            }
        }

        // 评论限流：每分钟最多5条评论
        if ("/Comment/save".equals(uri) && "POST".equalsIgnoreCase(method)) {
            String commentKey = "rate:user:" + userId + ":comment";
            Long commentResult = stringRedisTemplate.execute(
                    rateLimitScript,
                    Collections.singletonList(commentKey),
                    "5",       // 阈值：5条评论
                    "60"       // 60秒
            );
            
            if (commentResult != null && commentResult == 0) {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":429,\"message\":\"评论过于频繁，每分钟最多发5条评论\"}");
                return false;
            }
        }

        return true;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }
}
