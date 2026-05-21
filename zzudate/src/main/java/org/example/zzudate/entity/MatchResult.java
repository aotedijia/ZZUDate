package org.example.zzudate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("match_result")
public class MatchResult {
    @TableId(type = IdType.AUTO)
    private String id;
    private String userIdA;
    private String userIdB;
    private String userNameA;
    private String userNameB;
    private String userAnswerA;
    private String userAnswerB;
    private Double score;
    private String description;//deepseek生成的描述
    private String numberA;//A联系方式
    private String numberB;//B联系方式
}
