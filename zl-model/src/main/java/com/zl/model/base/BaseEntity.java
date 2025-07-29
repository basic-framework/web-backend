package com.zl.model.base;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 基础字段
 * @Author GuihaoLv
 */
@Data
public class BaseEntity {
    private Long id; //主键
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //更新时间
    private Long createBy; //创建人
    private Long updateBy; //更新人
    private String remark; //备注
    private Integer status; //状态 0：正常 1：停用

}