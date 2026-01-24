package com.zl.generator.controller;

import com.zl.common.result.Result;
import com.zl.generator.dto.CodeGenerateDto;
import com.zl.generator.service.CodeGenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@RestController
@RequestMapping("/generator/code")
@Tag(name = "代码生成管理", description = "代码生成管理接口")
@Slf4j
public class CodeGenController {

    @Autowired
    private CodeGenService codeGenService;

    /**
     * 根据表名获取代码生成元数据
     *
     * @param tableName 表名
     * @return 元数据
     */
    @GetMapping("/getByTableName")
    @Operation(summary = "获取表元数据")
    public Result<Map<String, Object>> getByTableName(@RequestParam String tableName) {
        Map<String, Object> metaData = codeGenService.previewCode(tableName);
        return Result.success(metaData);
    }

    /**
     * 批量生成后端代码
     *
     * @param codeGenerateDto 代码生成参数
     * @return 生成结果
     */
    @PostMapping("/batchGenCode")
    @Operation(summary = "批量生成后端代码")
    public Result<Map<String, Object>> batchGenCode(@RequestBody CodeGenerateDto codeGenerateDto) {
        Map<String, Object> result = codeGenService.batchGenCode(codeGenerateDto);
        return Result.success(result);
    }

    /**
     * 生成前端代码
     *
     * @param codeGenerateDto 代码生成参数
     * @return 生成结果
     */
    @PostMapping("/batchGenFrontendCode")
    @Operation(summary = "生成前端代码")
    public Result<Map<String, Object>> batchGenFrontendCode(@RequestBody CodeGenerateDto codeGenerateDto) {
        Map<String, Object> result = codeGenService.batchGenFrontendCode(codeGenerateDto);
        return Result.success(result);
    }

    /**
     * 预览生成的代码
     *
     * @param tableName 表名
     * @return 预览结果
     */
    @GetMapping("/previewCode")
    @Operation(summary = "预览代码")
    public Result<Map<String, Object>> previewCode(@RequestParam String tableName) {
        Map<String, Object> preview = codeGenService.previewCode(tableName);
        return Result.success(preview);
    }

    /**
     * 下载生成的代码
     *
     * @param tableName 表名
     * @return ZIP压缩包
     */
    @GetMapping("/downloadCode")
    @Operation(summary = "下载代码")
    public org.springframework.http.ResponseEntity<byte[]> downloadCode(@RequestParam String tableName) {
        try {
            byte[] zipBytes = codeGenService.downloadCode(tableName);

            String fileName = URLEncoder.encode(tableName + "_code.zip", StandardCharsets.UTF_8);

            return org.springframework.http.ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        } catch (Exception e) {
            log.error("下载代码失败", e);
            throw new RuntimeException("下载代码失败: " + e.getMessage());
        }
    }
}
