package com.zl.web.service;

import com.zl.model.entity.Resource;

import java.util.List;

public interface ResourceService {
    List<Resource> getResourceListByUserId(String id);
}
