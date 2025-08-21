package com.zl.web.service;

import com.zl.model.entity.security.Role;

import java.util.List;

public interface RoleService {
    /**
     * 根据用户id获取角色列表
     * @param id
     * @return
     */
    List<Role> getRoleListByUserId(String id);
}
