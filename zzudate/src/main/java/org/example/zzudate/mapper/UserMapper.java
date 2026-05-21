package org.example.zzudate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.zzudate.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
