package com.zl.web.service;

import com.zl.model.entity.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleService {
    /**
     * 根据用户id获取角色列表
     * @param id
     * @return
     */
    List<Role> getRoleListByUserId(String id);
}
