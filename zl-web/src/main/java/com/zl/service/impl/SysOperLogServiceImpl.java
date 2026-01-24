package com.zl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zl.domain.SysOperLog;
import com.zl.dto.SysOperLogDto;
import com.zl.mapper.SysOperLogMapper;
import com.zl.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志记录 Service 实现
 *
 * @author code-generator
 * @date 2026-01-24 10:54:33
 */
@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    @Override
    public List<SysOperLog> findAll() {
        return sysOperLogMapper.selectAll();
    }

    @Override
    public SysOperLog findById(Long id) {
        return sysOperLogMapper.selectById(id);
    }

    @Override
    public Boolean create(SysOperLogDto sysOperLogDto) {
        SysOperLog sysOperLog = BeanUtil.copyProperties(sysOperLogDto, SysOperLog.class);
        int result = sysOperLogMapper.insert(sysOperLog);
        return result > 0;
    }

    @Override
    public Boolean update(SysOperLogDto sysOperLogDto) {
        SysOperLog sysOperLog = BeanUtil.copyProperties(sysOperLogDto, SysOperLog.class);
        int result = sysOperLogMapper.update(sysOperLog);
        return result > 0;
    }

    @Override
    public Boolean deleteById(Long id) {
        int result = sysOperLogMapper.deleteById(id);
        return result > 0;
    }
}
