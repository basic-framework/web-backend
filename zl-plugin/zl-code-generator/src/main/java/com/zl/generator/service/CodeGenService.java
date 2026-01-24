package com.zl.generator.service;

import com.zl.generator.dto.CodeGenerateDto;

import java.util.Map;

/**
 * 代码生成 Service 接口
 *
 * @author code-generator
 * @date 2026-01-23
 */
public interface CodeGenService {

    /**
     * 批量生成后端代码
     *
     * @param codeGenerateDto 代码生成参数
     * @return 生成结果
     */
    Map<String, Object> batchGenCode(CodeGenerateDto codeGenerateDto);

    /**
     * 生成前端代码
     *
     * @param codeGenerateDto 代码生成参数
     * @return 生成结果
     */
    Map<String, Object> batchGenFrontendCode(CodeGenerateDto codeGenerateDto);

    /**
     * 预览生成的代码
     *
     * @param tableName 表名
     * @return 生成结果
     */
    Map<String, Object> previewCode(String tableName);

    /**
     * 下载生成的代码
     *
     * @param tableName 表名
     * @return 压缩包字节数组
     */
    byte[] downloadCode(String tableName);
}
