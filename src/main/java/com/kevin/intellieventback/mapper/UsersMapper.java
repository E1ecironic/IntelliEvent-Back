package com.kevin.intellieventback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kevin.intellieventback.domin.entity.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2025-12-21
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}
