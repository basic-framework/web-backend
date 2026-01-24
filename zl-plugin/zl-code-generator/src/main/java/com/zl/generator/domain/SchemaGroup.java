package com.zl.generator.domain;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据模型分组实体类
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchemaGroup extends BaseEntity {
    /**
     * 分组编码
     */
    private String code;

    /**
     * 分组名称
     */
    private String name;
}
