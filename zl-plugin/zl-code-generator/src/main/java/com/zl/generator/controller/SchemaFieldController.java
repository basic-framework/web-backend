package com.zl.generator.controller;

import com.zl.common.result.Result;
import com.zl.generator.domain.SchemaField;
import com.zl.generator.service.SchemaFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模型字段控制器
 *
 * @author code-generator
 * @date 2026-01-23
 */
@RestController
@RequestMapping("/generator/schemaField")
@Tag(name = "模型字段管理", description = "模型字段管理接口")
@Slf4j
public class SchemaFieldController {

    @Autowired
    private SchemaFieldService schemaFieldService;

    /**
     * 根据模型ID查询字段列表
     *
     * @param schemaId 模型ID
     * @return 字段列表
     */
    @GetMapping("/list/{schemaId}")
    @Operation(summary = "根据模型ID查询字段")
    public Result<List<SchemaField>> listBySchemaId(@PathVariable Long schemaId) {
        List<SchemaField> list = schemaFieldService.findBySchemaId(schemaId);
        return Result.success(list);
    }

    /**
     * 根据ID查询字段
     *
     * @param id 字段ID
     * @return 字段对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询字段")
    public Result<SchemaField> getById(@PathVariable Long id) {
        SchemaField schemaField = schemaFieldService.findById(id);
        return Result.success(schemaField);
    }

    /**
     * 批量插入字段
     *
     * @param schemaFieldList 字段列表
     * @return 是否成功
     */
    @PostMapping("/batch")
    @Operation(summary = "批量插入字段")
    public Result<Boolean> batchInsert(@RequestBody List<SchemaField> schemaFieldList) {
        Boolean flag = schemaFieldService.batchInsert(schemaFieldList);
        return Result.success(flag);
    }

    /**
     * 批量更新字段
     *
     * @param schemaFieldList 字段列表
     * @return 是否成功
     */
    @PutMapping("/batch")
    @Operation(summary = "批量更新字段")
    public Result<Boolean> batchUpdate(@RequestBody List<SchemaField> schemaFieldList) {
        Boolean flag = schemaFieldService.batchUpdate(schemaFieldList);
        return Result.success(flag);
    }

    /**
     * 更新字段
     *
     * @param schemaField 字段对象
     * @return 是否成功
     */
    @PutMapping("/single")
    @Operation(summary = "更新字段")
    public Result<Boolean> update(@RequestBody SchemaField schemaField) {
        Boolean flag = schemaFieldService.update(schemaField);
        return Result.success(flag);
    }

    /**
     * 根据ID删除字段
     *
     * @param id 字段ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除字段")
    public Result<Boolean> delete(@PathVariable Long id) {
        Boolean flag = schemaFieldService.deleteById(id);
        return Result.success(flag);
    }
}
