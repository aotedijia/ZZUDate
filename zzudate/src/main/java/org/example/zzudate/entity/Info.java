package org.example.zzudate.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Info {
    private String id;//帖子ID
    private String userId;//发布者ID
    private String userName;//发布者昵称
    private String title;//标题
    private String content;//内容
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
    private String category;//分区 公共讨论区 校医院 餐厅 学习备考
}
