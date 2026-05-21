package org.example.zzudate.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import org.example.zzudate.entity.Comment;
import org.example.zzudate.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

   @Autowired
   private CommentMapper commentMapper;

   public int saveComment(Comment comment) {
       return commentMapper.insert(comment);
   }

   public int deleteComment(String id) {
       return commentMapper.deleteById(id);
   }

   public List<Comment> listByInfoId(String infoId) {
      QueryWrapper<Comment> wrapper=new QueryWrapper();
      ((QueryWrapper)wrapper.eq("info_id",infoId)).orderByAsc("create_time");
      return commentMapper.selectList(wrapper);
   }
}
