package com.zl.web.service.impl;

import com.zl.model.entity.security.Resource;
import com.zl.model.entity.security.Role;
import com.zl.web.mapper.ResourceMapper;
import com.zl.web.mapper.RoleMapper;
import com.zl.web.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     *  根据用户id获取资源列表
     * @param id
     * @return
     */
    public List<Resource> getResourceListByUserId(String id) {
        Long userId=Long.parseLong(id);
        List<Role> roles=roleMapper.getRoleListByUserId(userId);
        List<Resource> resourceList=new ArrayList<>();
        for (Role role : roles) {
            Long roleId=role.getId();
            List<Resource> resources=resourceMapper.getResourceByRoleId(roleId);
            //去重
            for (Resource resource : resources) {
                if (!resourceList.contains(resource)) {
                    resourceList.add(resource);
                }
            }
        }
        return resourceList;
    }
}
