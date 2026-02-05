package com.kevin.intellieventback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kevin.intellieventback.domin.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 分页查询用户列表，包含关联的组织ID
     */
    IPage<Users> selectUserPageWithOrg(Page<Users> page, @Param("user") Users user);

}
