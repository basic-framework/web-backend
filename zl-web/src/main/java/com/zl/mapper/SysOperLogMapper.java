package com.zl.mapper;

import com.zl.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志记录 Mapper 接口
 *
 * @author code-generator
 * @date 2026-01-24 10:54:33
 */
@Mapper
public interface SysOperLogMapper {

    /**
     * 查询所有列表
     *
     * @return 列表
     */
    List<SysOperLog> selectAll();

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 对象
     */
    SysOperLog selectById(Long id);

    /**
     * 插入
     *
     * @param sysOperLog 对象
     * @return 影响行数
     */
    int insert(SysOperLog sysOperLog);

    /**
     * 更新
     *
     * @param sysOperLog 对象
     * @return 影响行数
     */
    int update(SysOperLog sysOperLog);

    /**
     * 根据ID删除
     *
     * @param id ID
     * @return 影响行数
     */
    int deleteById(Long id);
}
