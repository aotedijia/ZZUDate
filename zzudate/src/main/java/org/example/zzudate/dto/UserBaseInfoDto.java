package org.example.zzudate.dto;

import java.util.List;

import lombok.Data;
import lombok.Generated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

@Data
public class UserBaseInfoDto {
   private String id;
   
   @NotBlank(message = "昵称或姓名不能为空")
   private String name;
   
   @NotBlank(message = "联系方式不能为空")
   private String number;
   
   @NotNull(message = "性别不能为空")
   private Boolean gender;
   
   @NotBlank(message = "身高不能为空")
   private String height;
   
   @NotBlank(message = "学院不能为空")
   private String college;
   
   @NotBlank(message = "校区不能为空")
   private String campus;
   
   @NotNull(message = "就读层次不能为空")
   private Integer grade;
   
   @NotBlank(message = "倾向匹配不能为空")
   private String choose;
   
   @NotBlank(message = "年龄不能为空")
   private String age;
   
   @NotBlank(message = "交友意向不能为空")
   private String friendChoose;
   
   @NotBlank(message = "期望年龄最小值不能为空")
   private String ageRequirementMin;
   
   @NotBlank(message = "期望年龄最大值不能为空")
   private String ageRequirementMax;
   
   @NotBlank(message = "期望身高最小值不能为空")
   private String heightRequirementMin;
   
   @NotBlank(message = "期望身高最大值不能为空")
   private String heightRequirementMax;
   
   @NotEmpty(message = "期望校区不能为空")
   private List<String> campusRequirement;
}
