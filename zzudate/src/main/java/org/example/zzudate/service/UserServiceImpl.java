package org.example.zzudate.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.zzudate.dto.UserBaseInfoDto;
import org.example.zzudate.dto.UserSoulInfoDto;
import org.example.zzudate.entity.User;
import org.example.zzudate.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public User getUserByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,email);
        return userMapper.selectOne(wrapper);}

    public User getUserById(String id) {
        return userMapper.selectById(id);
    }

    public void saveUser(User user) {
        userMapper.insert(user);}

    public int saveBaseInfo(UserBaseInfoDto userBaseInfoDto) {
        log.info("收到同步请求，数据内容：" + userBaseInfoDto.toString());
        User user=new User();
        user.setId(userBaseInfoDto.getId());
        user.setNumber(userBaseInfoDto.getNumber());
        user.setName(userBaseInfoDto.getName());
        user.setGender(userBaseInfoDto.getGender());
        user.setHeight(userBaseInfoDto.getHeight());
        user.setCollege(userBaseInfoDto.getCollege());
        user.setCampus(userBaseInfoDto.getCampus());
        user.setGrade(userBaseInfoDto.getGrade());
        user.setChoose(userBaseInfoDto.getChoose());
        user.setAge(userBaseInfoDto.getAge());
        user.setFriendChoose(userBaseInfoDto.getFriendChoose());
        user.setAgeRequirementMin(userBaseInfoDto.getAgeRequirementMin());
        user.setAgeRequirementMax(userBaseInfoDto.getAgeRequirementMax());
        user.setHeightRequirementMin(userBaseInfoDto.getHeightRequirementMin());
        user.setHeightRequirementMax(userBaseInfoDto.getHeightRequirementMax());
        user.setCampusRequirement(userBaseInfoDto.getCampusRequirement());

        int result=userMapper.updateById(user);
        if(result>0) {
            log.info("基础信息同步成功，用户ID:"+user.getId());
        }
        return result;
    }

    public int saveSoulInfo(UserSoulInfoDto userSoulInfoDto) {
        User user=new User();
        user.setId(userSoulInfoDto.getUserId());
        user.setAnswers(userSoulInfoDto.getAnswers());
        int result = userMapper.updateById(user);
        if(result>0) {
            log.info("深度信息同步成功，用户ID:"+user.getId());
        }
        return result;
    }

    @Override
    public void addExpWithDailyCap(String userId, long exp, long dailyCap, String source) {
        String today = LocalDate.now().toString();
        String redisKey = "daily:exp:" + userId + ":" + source + ":" + today;

        // 获取今天已获得的经验值
        String currentExpStr = stringRedisTemplate.opsForValue().get(redisKey);
        long currentExp = currentExpStr != null ? Long.parseLong(currentExpStr) : 0L;

        // 如果已达到每日上限，不再增加
        if (currentExp >= dailyCap) {
            return;
        }

        // 计算实际可增加的经验值
        long expToAdd = Math.min(exp, dailyCap - currentExp);

        // 增加用户经验值
        User user = userMapper.selectById(userId);
        if (user != null) {
            Long userExp = user.getExp() != null ? user.getExp() : 0L;
            user.setExp(userExp + expToAdd);
            userMapper.updateById(user);
        }

        // 更新Redis中的计数
        stringRedisTemplate.opsForValue().increment(redisKey, expToAdd);

        // 设置过期时间（第一次设置时）
        if (stringRedisTemplate.getExpire(redisKey) == -1) {
            stringRedisTemplate.expire(redisKey, 24, TimeUnit.HOURS);
        }
    }

}
