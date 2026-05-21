package org.example.zzudate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.zzudate.entity.MatchResult;
import org.example.zzudate.entity.User;
import org.example.zzudate.mapper.MatchResultMapper;
import org.example.zzudate.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WeeklyMatchService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MatchResultMapper matchResultMapper;
    @Autowired
    private Match match;
    @Autowired
    private DeepSeekService deepSeekService;

    /**
     * 每周三 19:00 执行全量灵魂匹配
     * 流程：清空旧结果 → 预存画像 → 计算候选对 → 贪心配对 → 写入DB → 异步AI评语
     */
    @Scheduled(cron = "0 0 19 ? * WED")
    @Transactional
    public void runWeeklySoulMatch() {
        long startTime = System.currentTimeMillis();

        // 1. 清空上周匹配结果（事务内，后续失败会回滚）
        matchResultMapper.delete(null);

        // 2. 拉取已填写问卷的用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(User::getAnswers).ne(User::getAnswers, "");
        List<User> userList = userMapper.selectList(queryWrapper);

        if (userList == null || userList.size() < 2) {
            log.info("[匹配] 画像池不足2人，跳过本周匹配");
            return;
        }

        // 3. 预存画像缓存，避免循环内重复解析 JSON
        Map<String, Map<Integer, String>> portraitCache = new HashMap<>();
        for (User u : userList) {
            portraitCache.put(u.getId(), parsePortrait(u.getAnswers()));
        }

        // 4. 计算所有合法候选对的分数
        List<double[]> candidatePairs = new ArrayList<>(); // [indexA, indexB, score]
        for (int i = 0; i < userList.size(); i++) {
            User userA = userList.get(i);
            for (int j = i + 1; j < userList.size(); j++) {
                User userB = userList.get(j);
                if (!canMatch(userA, userB)) {
                    continue;
                }
                double score = match.calculateMatch(
                        portraitCache.get(userA.getId()),
                        portraitCache.get(userB.getId())
                );

                // 同学院加分（+2%），封顶100%
                if (userA.getCollege() != null && userB.getCollege() != null
                        && userA.getCollege().equals(userB.getCollege())) {
                    score = Math.min(1.0, score + 0.02);
                    log.debug("[加分] {} & {} 同学院({})，加2%", userA.getName(), userB.getName(), userA.getCollege());
                }

                candidatePairs.add(new double[]{i, j, score});
            }
        }

        // 5. 按分数从高到低排序，全局贪心配对
        candidatePairs.sort((a, b) -> Double.compare(b[2], a[2]));

        Set<String> processedUsers = new HashSet<>();
        List<MatchResult> finalResults = new ArrayList<>();

        for (double[] pair : candidatePairs) {
            User userA = userList.get((int) pair[0]);
            User userB = userList.get((int) pair[1]);
            double score = pair[2];

            if (processedUsers.contains(userA.getId()) || processedUsers.contains(userB.getId())) {
                continue;
            }
            if (score < 0.3) {
                continue; // 低于30%阈值不配对
            }

            MatchResult result = new MatchResult();
            result.setId(UUID.randomUUID().toString());
            result.setUserIdA(userA.getId());
            result.setUserIdB(userB.getId());
            result.setUserNameA(userA.getName());
            result.setUserNameB(userB.getName());
            result.setUserAnswerA(userA.getAnswers());
            result.setUserAnswerB(userB.getAnswers());
            result.setScore(score * 100);
            // 生成备用评语（AI评语生成后异步覆盖）
            result.setDescription(generateRealDescription(
                    score * 100,
                    portraitCache.get(userA.getId()),
                    portraitCache.get(userB.getId())
            ));

            finalResults.add(result);
            processedUsers.add(userA.getId());
            processedUsers.add(userB.getId());
        }

        // 6. 批量写入数据库
        for (MatchResult mr : finalResults) {
            matchResultMapper.insert(mr);
        }

        // 7. 异步调用 AI 生成评语（方法返回后事务提交，回调在独立事务中更新）
        for (MatchResult mr : finalResults) {
            String readableA = QuestionDictionary.convertToReadable(parsePortrait(mr.getUserAnswerA()));
            String readableB = QuestionDictionary.convertToReadable(parsePortrait(mr.getUserAnswerB()));
            String resultId = mr.getId();
            double score = mr.getScore();

            deepSeekService.generateMatchCommentAsync(readableA, readableB, score)
                    .thenAccept(aiComment -> {
                        MatchResult updateObj = new MatchResult();
                        updateObj.setId(resultId);
                        updateObj.setDescription(aiComment);
                        matchResultMapper.updateById(updateObj);
                        log.info("[AI评语] 已为 {} & {} 生成AI评语", mr.getUserNameA(), mr.getUserNameB());
                    })
                    .exceptionally(ex -> {
                        log.warn("[AI评语] 生成失败，保留备用评语: {}", ex.getMessage());
                        return null;
                    });
        }

        analyzeMetrics(startTime, userList.size(), finalResults.size());
    }

    /**
     * 判断两个用户是否可以配对
     * 校验顺序：双向性别倾向 → 年龄要求 → 身高要求 → 校区要求 → 交友意向
     */
    private boolean canMatch(User userA, User userB) {
        // 1. 性别倾向双向校验
        if (!checkGenderPreference(userA, userB)) return false;
        if (!checkGenderPreference(userB, userA)) return false;

        // 2. 年龄要求双向校验
        if (!checkAgeRequirement(userA, userB)) return false;
        if (!checkAgeRequirement(userB, userA)) return false;

        // 3. 身高要求双向校验
        if (!checkHeightRequirement(userA, userB)) return false;
        if (!checkHeightRequirement(userB, userA)) return false;

        // 4. 校区要求双向校验
        if (!checkCampusRequirement(userA, userB)) return false;
        if (!checkCampusRequirement(userB, userA)) return false;

        // 5. 交友意向校验
        if (!checkFriendChooseMatch(userA, userB)) {
            log.debug("[匹配过滤] {}(意向:{}) 与 {}(意向:{}) 交友意向不匹配",
                    userA.getName(), userA.getFriendChoose(),
                    userB.getName(), userB.getFriendChoose());
            return false;
        }

        return true;
    }

    /**
     * 校验 sourceUser 对 targetUser 的性别偏好
     * - 设置了choose：按偏好匹配（对方性别为null则无法匹配）
     * - 未设置choose：默认异性匹配（双方性别都不能为null）
     */
    private boolean checkGenderPreference(User sourceUser, User targetUser) {
        if (sourceUser.getChoose() != null) {
            boolean preferMale = "1".equals(sourceUser.getChoose());
            if (targetUser.getGender() == null) {
                log.debug("[匹配过滤] {}(偏好{}) 不能与 {}(未设置性别) 匹配",
                        sourceUser.getName(), preferMale ? "男" : "女", targetUser.getName());
                return false;
            }
            if (preferMale != targetUser.getGender()) {
                log.debug("[匹配过滤] {}(偏好{}) 不能与 {}(性别{}) 匹配：偏好不满足",
                        sourceUser.getName(), preferMale ? "男" : "女",
                        targetUser.getName(), targetUser.getGender() ? "男" : "女");
                return false;
            }
        } else {
            // 未设置偏好，默认异性匹配
            if (sourceUser.getGender() == null || targetUser.getGender() == null) {
                log.debug("[匹配过滤] {} 与 {} 性别信息不完整，无法默认异性匹配",
                        sourceUser.getName(), targetUser.getName());
                return false;
            }
            if (Objects.equals(sourceUser.getGender(), targetUser.getGender())) {
                log.debug("[匹配过滤] {} 与 {} 默认异性匹配但性别相同",
                        sourceUser.getName(), targetUser.getName());
                return false;
            }
        }
        return true;
    }

    /**
     * 校验 sourceUser 对 targetUser 的年龄要求
     */
    private boolean checkAgeRequirement(User sourceUser, User targetUser) {
        if (sourceUser.getAgeRequirementMin() == null || sourceUser.getAgeRequirementMax() == null) {
            return true; // 未设置年龄要求，放行
        }
        if (targetUser.getAge() == null) {
            return false; // 对方未设置年龄，无法满足要求
        }
        try {
            int targetAge = Integer.parseInt(targetUser.getAge());
            int minAge = Integer.parseInt(sourceUser.getAgeRequirementMin());
            int maxAge = Integer.parseInt(sourceUser.getAgeRequirementMax());
            if (targetAge < 16 || targetAge > 60) {
                return false;
            }
            if (targetAge < minAge || targetAge > maxAge) {
                log.debug("[匹配过滤] {}(年龄要求{}-{}) 与 {}(年龄{}) 不匹配",
                        sourceUser.getName(), minAge, maxAge, targetUser.getName(), targetAge);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 校验 sourceUser 对 targetUser 的身高要求
     */
    private boolean checkHeightRequirement(User sourceUser, User targetUser) {
        if (sourceUser.getHeightRequirementMin() == null || sourceUser.getHeightRequirementMax() == null) {
            return true; // 未设置身高要求，放行
        }
        if (targetUser.getHeight() == null) {
            return false; // 对方未设置身高，无法满足要求
        }
        try {
            int targetHeight = Integer.parseInt(targetUser.getHeight());
            int minHeight = Integer.parseInt(sourceUser.getHeightRequirementMin());
            int maxHeight = Integer.parseInt(sourceUser.getHeightRequirementMax());
            if (targetHeight < 140 || targetHeight > 220) {
                return false;
            }
            if (targetHeight < minHeight || targetHeight > maxHeight) {
                log.debug("[匹配过滤] {}(身高要求{}-{}) 与 {}(身高{}) 不匹配",
                        sourceUser.getName(), minHeight, maxHeight, targetUser.getName(), targetHeight);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 校验 sourceUser 对 targetUser 的校区要求
     * 优先级：campusRequirement（明确指定列表） > campus（同校区默认）
     */
    private boolean checkCampusRequirement(User sourceUser, User targetUser) {
        if (sourceUser.getCampusRequirement() != null && !sourceUser.getCampusRequirement().isEmpty()) {
            // 设置了明确的校区要求列表
            if (targetUser.getCampus() == null) {
                return false;
            }
            if (!sourceUser.getCampusRequirement().contains(targetUser.getCampus())) {
                log.debug("[匹配过滤] {}(校区要求{}) 与 {}(校区{}) 不匹配",
                        sourceUser.getName(), sourceUser.getCampusRequirement(),
                        targetUser.getName(), targetUser.getCampus());
                return false;
            }
        } else if (sourceUser.getCampus() != null && targetUser.getCampus() != null) {
            // 未设置校区要求，但双方都有校区 → 默认同校区
            if (!sourceUser.getCampus().equals(targetUser.getCampus())) {
                log.debug("[匹配过滤] {}(校区{}) 与 {}(校区{}) 不在同一校区",
                        sourceUser.getName(), sourceUser.getCampus(),
                        targetUser.getName(), targetUser.getCampus());
                return false;
            }
        }
        return true;
    }

    /**
     * 交友意向匹配
     * "顺其自然拓宽社交" 可与任意意向匹配
     * "寻找长期恋爱伴侣" 和 "寻找志同道合的朋友" 必须双方一致
     */
    private boolean checkFriendChooseMatch(User userA, User userB) {
        String friendChooseA = userA.getFriendChoose();
        String friendChooseB = userB.getFriendChoose();

        if (friendChooseA == null || friendChooseB == null) {
            return true; // 未设置意向，放行
        }

        String CASUAL = "顺其自然拓宽社交";
        if (CASUAL.equals(friendChooseA) || CASUAL.equals(friendChooseB)) {
            return true; // 任一方为"顺其自然"，都可匹配
        }
        return friendChooseA.equals(friendChooseB);
    }

    /**
     * 将 JSON 字符串解析为画像 Map
     */
    private Map<Integer, String> parsePortrait(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        return JSON.parseObject(json, new TypeReference<Map<Integer, String>>() {});
    }

    /**
     * 基于 40 维画像生成匹配描述（备用评语，AI 评语生成后异步覆盖）
     */
    public String generateRealDescription(double totalScore, Map<Integer, String> mapA, Map<Integer, String> mapB) {
        int materialMatches = countMatches(mapA, mapB, 1, 8);     // 物质底色 Q1-Q8
        int spiritualMatches = countMatches(mapA, mapB, 9, 18);   // 精神依恋 Q9-Q18
        int lifestyleMatches = countMatches(mapA, mapB, 19, 28);  // 生活节律 Q19-Q28
        int soulMatches = countMatches(mapA, mapB, 29, 40);       // 灵魂底线 Q29-Q40

        // 找到共鸣最强的维度
        String strongestDimension = "灵魂底线";
        int max = soulMatches;
        if (materialMatches > max) { strongestDimension = "物质底色"; max = materialMatches; }
        if (spiritualMatches > max) { strongestDimension = "精神依恋"; max = spiritualMatches; }
        if (lifestyleMatches > max) { strongestDimension = "生活节律"; }

        StringBuilder desc = new StringBuilder();
        desc.append("基于 40 维画像分析，你们的契合度达到 ").append(String.format("%.1f", totalScore)).append("%。");

        if (totalScore >= 60) {
            desc.append("这是一场跨越维度的相遇。你们在「").append(strongestDimension).append("」上表现出惊人的一致，");
            desc.append("仿佛早已习惯了彼此的思考方式。");
        } else {
            desc.append("虽然性格各有千秋，但「").append(strongestDimension).append("」是你们之间最稳固的桥梁，");
            desc.append("这种共鸣让你们在万千灵魂中脱颖而出。");
        }

        // 忠诚观彩蛋（Q32）
        if (Objects.equals(mapA.get(32), mapB.get(32)) && mapA.get(32) != null) {
            desc.append(" 值得一提的是，你们对「忠诚」有着相同的敬畏。");
        }

        return desc.toString();
    }

    /**
     * 统计指定题号区间内答案完全一致的数量
     */
    private int countMatches(Map<Integer, String> a, Map<Integer, String> b, int start, int end) {
        int count = 0;
        for (int i = start; i <= end; i++) {
            if (Objects.equals(a.get(i), b.get(i)) && a.get(i) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * 匹配指标分析报告
     */
    private void analyzeMetrics(long startTime, int totalUsers, int matchPairs) {
        long duration = System.currentTimeMillis() - startTime;
        String coverage = totalUsers > 0
                ? String.format("%.1f", matchPairs * 2.0 / totalUsers * 100)
                : "0";
        log.info("--- ZZUDate 每周三 19:00 灵魂匹配指标报告 ---");
        log.info("[性能] 计算与数据库写入总耗时: {}ms", duration);
        log.info("[规模] 画像池总数: {} | 成功配对对数: {}", totalUsers, matchPairs);
        log.info("[真诚] 匹配覆盖率: {}%", coverage);
        log.info("------------------------------------------");
    }
}
