package org.example.zzudate.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import org.example.zzudate.entity.Info;
import org.example.zzudate.mapper.InfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfoServiceImpl implements InfoService {
   @Autowired
   private InfoMapper infoMapper;

   public int saveInfo(Info info) {
       return infoMapper.insert(info);
   }

   public int updateInfo(Info info) {
       return infoMapper.updateById(info);
   }

   public int deleteInfo(String id) {
       return infoMapper.deleteById(id);
   }

   public List<Info> listAll() {
      QueryWrapper<Info> wrapper=new QueryWrapper<>();
      wrapper.orderByDesc("create_time");
      return infoMapper.selectList(wrapper);
   }

    public List<Info> listByCategory(String category) {
        QueryWrapper<Info> wrapper=new QueryWrapper<>();
        wrapper.eq(category!=null,"category",category)
                .orderByDesc("create_time");
        return infoMapper.selectList(wrapper);
    }

    public List<Info> listByUserId(String userId) {
        QueryWrapper<Info> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return infoMapper.selectList(wrapper);
    }
}
