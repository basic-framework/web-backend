package com.zl.security.service;

import com.zl.model.entity.security.Resource;

import java.util.List;

public interface ResourceService {
    List<Resource> getResourceListByUserId(String id);
}
