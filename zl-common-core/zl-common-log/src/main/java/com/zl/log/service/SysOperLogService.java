package com.zl.log.service;

import com.zl.log.event.OperLogEvent;
import com.zl.log.mapper.SysOperLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogService {
    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    public void insertOperlog(OperLogEvent operLog) {
        sysOperLogMapper.insertSelective(operLog);
    }
}
