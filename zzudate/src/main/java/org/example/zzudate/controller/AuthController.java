package org.example.zzudate.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.example.zzudate.EmailService;
import org.example.zzudate.GenerateEmailCode;
import org.example.zzudate.Result;
import org.example.zzudate.entity.User;
import org.example.zzudate.service.UserService;
import org.example.zzudate.utils.CurrentUser;
import org.example.zzudate.vo.TokenVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth"})
public class AuthController {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(AuthController.class);
   @Autowired
   private StringRedisTemplate stringRedisTemplate;
   @Autowired
   private EmailService emailService;
   @Autowired
   private UserService userService;
   private static final DefaultRedisScript<Long> AUTH_SCRIPT = new DefaultRedisScript();
   private static final List<String> ALLOWED_DOMAINS;

   @PostMapping({"/logout"})
   public Result logout(HttpServletRequest request) {
      String accessToken=request.getHeader("Authorization");
      if(accessToken!=null){
         String key="login:accessToken:"+accessToken;
         stringRedisTemplate.delete(key);
      }
      CurrentUser.remove();
      return Result.success("退出成功");
   }

   @PostMapping({"/getemailcode"})
   public Result getEmailCode(String email, HttpServletRequest request) {
      if(email==null){
         throw new IllegalArgumentException("邮箱不能为空");
      }else{
         String domain=email.substring(email.indexOf(64)+1);
         if(!ALLOWED_DOMAINS.contains(domain)){
            throw new IllegalArgumentException("非法域名");
         }else{
            String timeInSec=String.valueOf(System.currentTimeMillis()/1000L);
            String ip=getClientIp(request);
            String[] var10000=new String[]{"limit:all_email_send:"+timeInSec,"login:blacklist:"+ip,"login:emailcode:limit:"+email,null,null};
            LocalDate var10003 = LocalDate.now();
            var10000[3] = "login:emailcode:daily_limit:" + var10003 + ":" + ip;
            var10000[4] = "lock:getcode:" + email;
            List<String> keys = Arrays.asList(var10000);
            Long result = (Long)this.stringRedisTemplate.execute(AUTH_SCRIPT, keys, new Object[]{"5", "15", "5"});
            if (result != null && result == 1L) {
               String lockKey = "lock:getcode:" + email;
               try {
                  String emailcode=GenerateEmailCode.generateEmailcode();
                  emailService.sendCode(email,emailcode);
                  stringRedisTemplate.opsForValue().set("login:code:"+email,emailcode,5L,TimeUnit.MINUTES);
                  stringRedisTemplate.opsForValue().set("login:emailcode:limit:"+email,"1",60L,TimeUnit.SECONDS);
                  log.info("邮件下发成功: {}, IP: {}", email, ip);
                  return Result.success("验证码已发送");
               } catch (Exception e) {
                  log.error("邮件服务器故障: ", e);
                  this.stringRedisTemplate.delete(lockKey);
                  return Result.error("邮件发送失败，请稍后重试");
               }
            } else {
               this.handleInterceptLog(result, ip, email);
               return Result.error("请求过于频繁，请稍后再试");
            }
         }
      }
   }

   @PostMapping({"/emailloginautoregister"})
   public Result emailloginautoregister(String email, String emailCode) {
      log.info("邮箱登录/注册: {}", email);
      if (email != null && emailCode != null) {
         String key = "login:code:" + email;
         String emailcode = (String)this.stringRedisTemplate.opsForValue().get(key);
         if (emailcode != null && emailcode.equals(emailCode)) {
            this.stringRedisTemplate.delete(key);
            User user = this.userService.getUserByEmail(email);
            if (user == null) {
               user = new User();
               user.setEmail(email);
               user.setCreateTime(LocalDateTime.now());
               userService.saveUser(user);
            }

            String accessToken = UUID.randomUUID().toString();
            this.stringRedisTemplate.opsForValue().set("login:accessToken:" + accessToken, user.getId(), 48L, TimeUnit.HOURS);
            TokenVo tokenVo = new TokenVo();
            tokenVo.setAccessToken(accessToken);
            tokenVo.setUserId(user.getId());
            tokenVo.setEmail(user.getEmail());
            tokenVo.setName(user.getName());
            tokenVo.setExp(user.getExp() != null ? user.getExp() : 0L);
            return Result.success("登录成功", tokenVo);
         } else {
            return Result.error("验证码过期或验证码不正确");
         }
      } else {
         log.info("邮箱或者验证码不能为空");
         throw new IllegalArgumentException("邮箱或者验证码不能为空");
      }
   }

   private void handleInterceptLog(Long code, String ip, String email) {
      if (code == -1L) {
         log.warn("【全局熔断】触发接口上限拦截");
      } else if (code == -2L) {
         log.debug("【黑名单】拦截恶意IP: {}", ip);
      } else if (code == -4L) {
         log.warn("【IP超额】已自动拉黑IP: {}", ip);
      } else if (code == -5L) {
         log.info("【并发锁】邮箱 {} 请求冲突", email);
      }

   }

   private String getClientIp(HttpServletRequest request) {
      String ip = request.getHeader("X-Forwarded-For");
      if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getHeader("Proxy-Client-IP");
      }

      if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getHeader("WL-Proxy-Client-IP");
      }

      if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getRemoteAddr();
      }

      if (ip != null && ip.contains(",")) {
         ip = ip.split(",")[0].trim();
      }

      return ip;
   }

   static {
      AUTH_SCRIPT.setResultType(Long.class);
      AUTH_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/check_and_lock.lua")));
      ALLOWED_DOMAINS = List.of("gs.zzu.edu.cn", "zzu.edu.cn", "stu.zzu.edu.cn");
   }
}
