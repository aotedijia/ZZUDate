package org.example.zzudate.dto;

import lombok.Data;

@Data
public class UserAnswersDto {
    private Long userId;
    private String answers;//前端选择的答案转化的JSON字符串
}