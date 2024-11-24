package com.windsurf.common.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.windsurf.common.security.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
