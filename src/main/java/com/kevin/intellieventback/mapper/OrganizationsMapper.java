package com.kevin.intellieventback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kevin.intellieventback.domin.entity.Organizations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 组织架构表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2025-12-20
 */
@Mapper
public interface OrganizationsMapper extends BaseMapper<Organizations> {

    /**
     * 批量更新组织层级
     * @param ids 组织ID列表
     * @param diff 层级差值
     */
    @Update("<script>" +
            "UPDATE organizations SET level = level + #{diff} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void updateLevelDiff(@Param("ids") List<String> ids, @Param("diff") int diff);

}
