package com.zl.controller;

import com.zl.common.result.Result;
import com.zl.domain.SysOperLog;
import com.zl.dto.SysOperLogDto;
import com.zl.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志记录控制器
 *
 * @author code-generator
 * @date 2026-01-24 10:54:33
 */
@RestController
@RequestMapping("/system/sysOperLog")
@Tag(name = "操作日志记录管理", description = "操作日志记录管理接口")
@Slf4j
public class SysOperLogController {

    @Autowired
    private SysOperLogService sysOperLogService;

    /**
     * 查询所有列表
     *
     * @return 列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询列表")
    public Result<List<SysOperLog>> list() {
        List<SysOperLog> list = sysOperLogService.findAll();
        return Result.success(list);
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询")
    public Result<SysOperLog> getById(@PathVariable Long id) {
        SysOperLog sysOperLog = sysOperLogService.findById(id);
        return Result.success(sysOperLog);
    }

    /**
     * 创建
     *
     * @param sysOperLogDto 数据传输对象
     * @return 是否成功
     */
    @PostMapping
    @Operation(summary = "创建")
    public Result<Boolean> create(@RequestBody SysOperLogDto sysOperLogDto) {
        Boolean flag = sysOperLogService.create(sysOperLogDto);
        return Result.success(flag);
    }

    /**
     * 更新
     *
     * @param sysOperLogDto 数据传输对象
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新")
    public Result<Boolean> update(@RequestBody SysOperLogDto sysOperLogDto) {
        Boolean flag = sysOperLogService.update(sysOperLogDto);
        return Result.success(flag);
    }

    /**
     * 根据ID删除
     *
     * @param id ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public Result<Boolean> delete(@PathVariable Long id) {
        Boolean flag = sysOperLogService.deleteById(id);
        return Result.success(flag);
    }
}
