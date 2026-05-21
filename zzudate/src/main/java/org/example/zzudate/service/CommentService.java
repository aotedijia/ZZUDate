package org.example.zzudate.service;

import java.util.List;
import org.example.zzudate.entity.Comment;

public interface CommentService {
   int saveComment(Comment comment);
   int deleteComment(String id);
   List<Comment> listByInfoId(String infoId);
}
