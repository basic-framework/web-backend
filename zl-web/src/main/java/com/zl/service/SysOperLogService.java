package com.zl.service;

import com.zl.domain.SysOperLog;
import com.zl.dto.SysOperLogDto;

import java.util.List;

/**
 * 操作日志记录 Service 接口
 *
 * @author code-generator
 * @date 2026-01-24 10:54:33
 */
public interface SysOperLogService {

    /**
     * 查询所有列表
     *
     * @return 列表
     */
    List<SysOperLog> findAll();

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 对象
     */
    SysOperLog findById(Long id);

    /**
     * 创建
     *
     * @param sysOperLogDto 数据传输对象
     * @return 是否成功
     */
    Boolean create(SysOperLogDto sysOperLogDto);

    /**
     * 更新
     *
     * @param sysOperLogDto 数据传输对象
     * @return 是否成功
     */
    Boolean update(SysOperLogDto sysOperLogDto);

    /**
     * 根据ID删除
     *
     * @param id ID
     * @return 是否成功
     */
    Boolean deleteById(Long id);
}
