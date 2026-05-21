package org.example.zzudate.entity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

@Data
@TableName(value = "user", autoResultMap = true)
public class User {
    private String id;//用户ID
    private String name;//昵称
    private String email;//邮箱
    private LocalDateTime createTime;//创建时间
    private String number;//联系方式
    private String age;//年龄
    private Boolean gender;//性别
    private String height;//身高
    private String college;//学院
    private String campus;//校区
    private Integer grade;//年级
    private String answers;//深度评测答案
    private String choose;//性别选择倾向
    private String friendChoose;//交友倾向
    private String ageRequirementMax;//最大接受年龄
    private String ageRequirementMin;//最小接受年龄
    private String heightRequirementMax;//最高接受身高
    private String heightRequirementMin;//最低接受身高
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> campusRequirement;//可接受校区
    @TableField("exp")
    private Long exp;//经验值
}