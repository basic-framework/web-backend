package com.zl.generator.controller;

import com.zl.common.result.Result;
import com.zl.generator.domain.SchemaGroup;
import com.zl.generator.service.SchemaGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据模型分组控制器
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@RestController
@RequestMapping("/generator/schemaGroup")
@Tag(name = "模型分组管理", description = "模型分组管理接口")
@Slf4j
public class SchemaGroupController {

    @Autowired
    private SchemaGroupService schemaGroupService;

    /**
     * 查询所有分组列表
     *
     * @return 分组列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询分组列表")
    public Result<List<SchemaGroup>> list() {
        List<SchemaGroup> list = schemaGroupService.findAll();
        return Result.success(list);
    }

    /**
     * 根据ID查询分组
     *
     * @param id 分组ID
     * @return 分组对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询分组")
    public Result<SchemaGroup> getById(@PathVariable Long id) {
        SchemaGroup schemaGroup = schemaGroupService.findById(id);
        return Result.success(schemaGroup);
    }

    /**
     * 创建分组
     *
     * @param schemaGroup 分组对象
     * @return 是否成功
     */
    @PostMapping
    @Operation(summary = "创建分组")
    public Result<Boolean> create(@RequestBody SchemaGroup schemaGroup) {
        Boolean flag = schemaGroupService.create(schemaGroup);
        return Result.success(flag);
    }

    /**
     * 更新分组
     *
     * @param schemaGroup 分组对象
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新分组")
    public Result<Boolean> update(@RequestBody SchemaGroup schemaGroup) {
        Boolean flag = schemaGroupService.update(schemaGroup);
        return Result.success(flag);
    }

    /**
     * 根据ID删除分组
     *
     * @param id 分组ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分组")
    public Result<Boolean> delete(@PathVariable Long id) {
        Boolean flag = schemaGroupService.deleteById(id);
        return Result.success(flag);
    }
}
