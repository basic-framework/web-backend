package com.zl.web.mapper;

import com.zl.model.entity.security.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper {

     /**
     * 根据用户id获取角色列表
     * @param
     * @return
     */
    @Select("select distinct sys_role.* from sys_role left join sys_user_role on sys_role.id=sys_user_role.role_id " +
            "where sys_user_role.user_id=#{userId}")
    List<Role> getRoleListByUserId(Long userId);



    /**
     * 插入用户角色关系
     * @param roleId
     */
    @Insert("insert into sys_user_role(role_id,user_id) values(#{roleId},#{userId})")
    Boolean insertRoleUser(Long roleId, Long userId);

    /**
     * 删除用户角色关系
     * @param userId
     */
    @Delete("delete from sys_user_role where user_id=#{userId}")
    Boolean deleteRoleUser(Long userId);
}
