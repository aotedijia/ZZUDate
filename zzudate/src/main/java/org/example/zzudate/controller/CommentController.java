package org.example.zzudate.controller;

import java.util.List;
import lombok.Generated;
import org.example.zzudate.Result;
import org.example.zzudate.entity.Comment;
import org.example.zzudate.service.CommentService;
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
@RequestMapping({"/Comment"})
public class CommentController {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(CommentController.class);
   @Autowired
   private CommentService commentService;
   @Autowired
   private UserService userService;

   @PostMapping({"/save"})
   public Result<Object> saveComment(@RequestBody Comment comment) {
      String currentUserId = CurrentUser.getUserId();
      log.info("用户{}评论帖子: {}", currentUserId, comment.getInfoId());
      comment.setUserId(currentUserId);
      if (this.commentService.saveComment(comment) > 0) {
         // 评论成功，增加经验值（和发帖一样：+20，每日上限100）
         this.userService.addExpWithDailyCap(currentUserId, 20L, 100L, "comment");
         return Result.success("评论成功");
      } else {
         return Result.error("评论失败");
      }
   }

   @PostMapping({"/delete"})
   public Result deleteComment(@RequestBody Comment comment) {
      String currentUserId = CurrentUser.getUserId();
      if (!comment.getUserId().equals(currentUserId)) {
         return Result.error("无权删除");
      } else {
         return this.commentService.deleteComment(comment.getId()) > 0 ? Result.success("删除成功") : Result.error("删除失败");
      }
   }

   @GetMapping({"/listByInfoId"})
   public Result<List<Comment>> listByInfoId(@RequestParam String infoId) {
      return Result.success(this.commentService.listByInfoId(infoId));
   }
}
