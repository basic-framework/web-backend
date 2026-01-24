package com.zl.generator.dto;

import lombok.Data;

/**
 * 代码生成 DTO
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Data
public class CodeGenerateDto {
    /**
     * 表名（多个用逗号分隔）
     */
    private String tableNameStr;

    /**
     * 前端项目路径（生成前端代码时使用）
     */
    private String workPath;

    /**
     * 生成命令（生成前端代码时使用）
     */
    private String previewCode;
}
