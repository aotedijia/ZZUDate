package org.example.zzudate.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import org.example.zzudate.Result;
import org.example.zzudate.dto.UserBaseInfoDto;
import org.example.zzudate.dto.UserSoulInfoDto;
import org.example.zzudate.entity.MatchResult;
import org.example.zzudate.entity.User;
import org.example.zzudate.mapper.MatchResultMapper;
import org.example.zzudate.mapper.UserMapper;
import org.example.zzudate.service.UserService;
import org.example.zzudate.utils.CurrentUser;
import org.example.zzudate.vo.MatchResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match")
public class MatchController {
   private static final Logger log = LoggerFactory.getLogger(MatchController.class);

   @Autowired
   private UserService userService;
   @Autowired
   private UserMapper userMapper;
   @Autowired
   private MatchResultMapper matchResultMapper;

   @GetMapping("/getuserinfo")
   public Result getUserInfo() {
      String userId = CurrentUser.getUserId();
      User user = userMapper.selectById(userId);
      if (user == null) {
         return Result.error("用户不存在");
      }
      log.info("获取用户信息成功: {}", user.getId());
      return Result.success(user);
   }

   @PostMapping("/savebaseinfo")
   public Result saveBaseInfo(@Valid @RequestBody UserBaseInfoDto userBaseInfoDto) {
      userBaseInfoDto.setId(CurrentUser.getUserId());
      log.info("收到基础信息同步请求,用户id为：{}", CurrentUser.getUserId());

      int ageMin = Integer.parseInt(userBaseInfoDto.getAgeRequirementMin());
      int ageMax = Integer.parseInt(userBaseInfoDto.getAgeRequirementMax());
      if (ageMin > ageMax) {
         return Result.error("期望年龄最小值不能大于最大值");
      }

      int heightMin = Integer.parseInt(userBaseInfoDto.getHeightRequirementMin());
      int heightMax = Integer.parseInt(userBaseInfoDto.getHeightRequirementMax());
      if (heightMin > heightMax) {
         return Result.error("期望身高最小值不能大于最大值");
      }

      int tem = userService.saveBaseInfo(userBaseInfoDto);
      return tem > 0 ? Result.success("同步成功") : Result.error("同步失败");
   }

   @PostMapping("/saveuserinfo")
   public Result saveUserInfo(@RequestBody UserSoulInfoDto userSoulInfoDto) {
      log.info("收到深度信息同步请求,用户id为：{}", CurrentUser.getUserId());
      int tem = userService.saveSoulInfo(userSoulInfoDto);
      return tem > 0 ? Result.success("同步成功") : Result.error("同步失败");
   }

   /**
    * 获取当前用户的匹配结果
    */
   @PostMapping("/getmatchresult")
   public Result getMatchResult(@RequestParam String userId) {
      log.info("收到匹配结果获取请求,用户id为：{}", CurrentUser.getUserId());
      if (!userId.equals(CurrentUser.getUserId())) {
         return Result.error("请不要攻击");
      }

      LambdaQueryWrapper<MatchResult> wrapper = new LambdaQueryWrapper<>();
      wrapper.and(w -> w.eq(MatchResult::getUserIdA, userId)
                        .or()
                        .eq(MatchResult::getUserIdB, userId));
      wrapper.orderByDesc(MatchResult::getId).last("LIMIT 1");

      MatchResult matchResult = matchResultMapper.selectOne(wrapper);
      if (matchResult == null) {
         return Result.success("本周未匹配到人");
      }

      MatchResultVo vo = buildMatchResultVo(userId, matchResult);
      return Result.success(vo);
   }

   /**
    * 坦白联系方式（将本人的联系方式写入匹配记录）
    */
   @PostMapping("/shownumber")
   public Result showNumber(@RequestParam String userId) {
      log.info("用户{}坦白联系方式", CurrentUser.getUserId());
      if (!userId.equals(CurrentUser.getUserId())) {
         return Result.error("请不要攻击");
      }

      User user = userMapper.selectById(userId);
      if (user == null || user.getNumber() == null) {
         return Result.error("未找到有效的联系方式，请先完善基础资料");
      }

      LambdaQueryWrapper<MatchResult> wrapper = new LambdaQueryWrapper<>();
      wrapper.and(w -> w.eq(MatchResult::getUserIdA, userId)
                        .or()
                        .eq(MatchResult::getUserIdB, userId));
      wrapper.orderByDesc(MatchResult::getId).last("LIMIT 1");

      MatchResult matchResult = matchResultMapper.selectOne(wrapper);
      if (matchResult == null) {
         return Result.error("本周暂无匹配结果，无法进行坦白");
      }

      if (userId.equals(matchResult.getUserIdA())) {
         matchResult.setNumberA(user.getNumber());
      } else {
         matchResult.setNumberB(user.getNumber());
      }

      matchResultMapper.updateById(matchResult);
      return Result.success("联系方式坦白成功");
   }

   /**
    * 构建匹配结果VO
    */
   private MatchResultVo buildMatchResultVo(String userId, MatchResult matchResult) {
      MatchResultVo vo = new MatchResultVo();
      vo.setUserId(userId);

      boolean isUserA = userId.equals(matchResult.getUserIdA());
      String otherUserId = isUserA ? matchResult.getUserIdB() : matchResult.getUserIdA();
      vo.setMatchId(otherUserId);
      vo.setMatchUserName(isUserA ? matchResult.getUserNameB() : matchResult.getUserNameA());
      vo.setScore(matchResult.getScore());
      vo.setDescription(matchResult.getDescription());

      boolean iHaveConfessed = isUserA
              ? matchResult.getNumberA() != null
              : matchResult.getNumberB() != null;
      boolean otherHasConfessed = isUserA
              ? matchResult.getNumberB() != null
              : matchResult.getNumberA() != null;

      vo.setIHaveNumber(iHaveConfessed);
      vo.setIHaveNumber2(otherHasConfessed);

      if (iHaveConfessed && otherHasConfessed) {
         String otherNumber = isUserA ? matchResult.getNumberB() : matchResult.getNumberA();
         vo.setNumber(otherNumber);
      }

      return vo;
   }
}
