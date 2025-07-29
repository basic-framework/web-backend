package com.zl.web.mapper;

import com.zl.model.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ResourceMapper {
    /**
     * 根据角色id获取资源
     * @param roleId
     * @return
     */
    @Select("SELECT distinct r.* from sys_resource r left join sys_role_resource rr " +
            "on r.resource_no=rr.resource_no where rr.role_id=#{roleId}")
    List<Resource> getResourceByRoleId(Long roleId);
}
