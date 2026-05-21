package org.example.zzudate.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private String id;//评论ID
    private String infoId;//关联的帖子ID
    private String userId;//评论者ID
    private String userName;//评论者昵称
    private String content;//评论内容
    private LocalDateTime createTime;//创建时间
}
