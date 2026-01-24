package com.zl.generator.controller;

import com.zl.common.result.Result;
import com.zl.generator.domain.Schema;
import com.zl.generator.service.SchemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据模型控制器
 *
 * @author code-generator
 * @date 2026-01-23
 */
@RestController
@RequestMapping("/generator/schema")
@Tag(name = "数据模型管理", description = "数据模型管理接口")
@Slf4j
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    /**
     * 查询所有模型列表
     *
     * @return 模型列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询模型列表")
    public Result<List<Schema>> list() {
        List<Schema> list = schemaService.findAll();
        return Result.success(list);
    }

    /**
     * 根据ID查询模型
     *
     * @param id 模型ID
     * @return 模型对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询模型")
    public Result<Schema> getById(@PathVariable Long id) {
        Schema schema = schemaService.findById(id);
        return Result.success(schema);
    }

    /**
     * 创建模型
     *
     * @param schema 模型对象
     * @return 是否成功
     */
    @PostMapping
    @Operation(summary = "创建模型")
    public Result<Boolean> create(@RequestBody Schema schema) {
        Boolean flag = schemaService.create(schema);
        return Result.success(flag);
    }

    /**
     * 更新模型
     *
     * @param schema 模型对象
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新模型")
    public Result<Boolean> update(@RequestBody Schema schema) {
        Boolean flag = schemaService.update(schema);
        return Result.success(flag);
    }

    /**
     * 根据ID删除模型
     *
     * @param id 模型ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除模型")
    public Result<Boolean> delete(@PathVariable Long id) {
        Boolean flag = schemaService.deleteById(id);
        return Result.success(flag);
    }

    /**
     * 从数据库表同步字段信息
     *
     * @param schemaId 模型ID
     * @return 是否成功
     */
    @PostMapping("/syncFields/{schemaId}")
    @Operation(summary = "同步表字段")
    public Result<Boolean> syncFields(@PathVariable Long schemaId) {
        Boolean flag = schemaService.syncFieldsFromTable(schemaId);
        return Result.success(flag);
    }

    /**
     * 根据分组ID查询模型列表
     *
     * @param schemaGroupId 分组ID
     * @return 模型列表
     */
    @GetMapping("/groupBy/{schemaGroupId}")
    @Operation(summary = "根据分组查询模型")
    public Result<List<Schema>> getByGroupId(@PathVariable Long schemaGroupId) {
        List<Schema> list = schemaService.findByGroupId(schemaGroupId);
        return Result.success(list);
    }

    /**
     * 获取数据库中的所有表
     *
     * @return 表名列表
     */
    @GetMapping("/dbTables")
    @Operation(summary = "获取数据库表列表")
    public Result<List<Map<String, String>>> getDatabaseTables() {
        List<Map<String, String>> tables = schemaService.getDatabaseTables();
        return Result.success(tables);
    }

    /**
     * 导入数据库表
     *
     * @param params 参数 {tableName: 表名, schemaGroupId: 分组ID}
     * @return 是否成功
     */
    @PostMapping("/importTable")
    @Operation(summary = "导入数据库表")
    public Result<Boolean> importTable(@RequestBody Map<String, Object> params) {
        String tableName = (String) params.get("tableName");
        Long schemaGroupId = params.get("schemaGroupId") != null ?
                Long.valueOf(params.get("schemaGroupId").toString()) : 1L;
        Boolean flag = schemaService.importTable(tableName, schemaGroupId);
        return Result.success(flag);
    }

    /**
     * 清理重复的字段数据
     *
     * @param schemaId 模型ID
     * @return 清理结果
     */
    @PostMapping("/cleanDuplicateFields/{schemaId}")
    @Operation(summary = "清理重复字段")
    public Result<Map<String, Object>> cleanDuplicateFields(@PathVariable Long schemaId) {
        Map<String, Object> result = schemaService.cleanDuplicateFields(schemaId);
        return Result.success(result);
    }
}
