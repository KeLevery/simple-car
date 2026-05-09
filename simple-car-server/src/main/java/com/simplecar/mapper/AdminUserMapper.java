package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
