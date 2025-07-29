package com.zl.web.service.impl;

import com.zl.model.entity.Role;
import com.zl.web.mapper.RoleMapper;
import com.zl.web.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 根据用户id获取角色列表
     * @param id
     * @return
     */
    @Override
    public List<Role> getRoleListByUserId(String id) {
        return roleMapper.getRoleListByUserId(Long.valueOf(id));
    }


}
