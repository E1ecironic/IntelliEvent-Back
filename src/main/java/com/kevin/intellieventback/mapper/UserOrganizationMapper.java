package com.kevin.intellieventback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户组织关系表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@Mapper
public interface UserOrganizationMapper extends BaseMapper<UserOrganization> {

}
