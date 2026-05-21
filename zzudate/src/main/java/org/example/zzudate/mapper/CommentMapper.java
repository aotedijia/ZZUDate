package org.example.zzudate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.zzudate.entity.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
