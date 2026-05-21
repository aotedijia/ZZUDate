package org.example.zzudate.controller;

import java.util.List;
import lombok.Generated;
import org.example.zzudate.Result;
import org.example.zzudate.entity.Info;
import org.example.zzudate.entity.User;
import org.example.zzudate.service.InfoService;
import org.example.zzudate.service.UserService;
import org.example.zzudate.utils.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/Info"})
public class InfoController {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(InfoController.class);
   @Autowired
   private InfoService infoService;
   @Autowired
   private UserService userService;

   @PostMapping({"/saveInfo"})
   public Result<Object> saveInfo(@RequestBody Info info) {
      String currentUserId = CurrentUser.getUserId();
      info.setUserId(currentUserId);
      if (this.infoService.saveInfo(info) <= 0) {
         return Result.<Object>error("新增失败");
      } else {
         this.userService.addExpWithDailyCap(currentUserId, 20L, 100L, "post");
         User user = this.userService.getUserById(currentUserId);
         long newExp = user != null && user.getExp() != null ? user.getExp() : 0L;
         return Result.<Object>success("新增成功", newExp);
      }
   }

   @PostMapping({"/updateInfo"})
   public Result updateInfo(@RequestBody Info info) {
      if (!CurrentUser.getUserId().equals(info.getUserId())) {
         return Result.error("请不要攻击");
      } else {
         return this.infoService.updateInfo(info) > 0 ? Result.success("更新成功") : Result.error("更新失败");
      }
   }

   @PostMapping({"/deleteInfo"})
   public Result deleteInfo(@RequestBody Info info) {
      if (!CurrentUser.getUserId().equals(info.getUserId())) {
         return Result.error("请不要攻击");
      } else {
         return this.infoService.deleteInfo(info.getId()) > 0 ? Result.success("删除成功") : Result.error("删除失败");
      }
   }

   @GetMapping({"/list"})
   public Result<List<Info>> listAll() {
      return Result.success(this.infoService.listAll());
   }

   @GetMapping({"/listByCategory"})
   public Result<List<Info>> listByCategory(@RequestParam String category) {
      log.info("按分类获取帖子: {}", category);
      return Result.success(this.infoService.listByCategory(category));
   }

   @GetMapping({"/listByUserId"})
   public Result<List<Info>> listByUserId(@RequestParam String userId) {
      return Result.success(this.infoService.listByUserId(userId));
   }
}
