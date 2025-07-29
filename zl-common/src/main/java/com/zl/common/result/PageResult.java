package com.zl.common.result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

 /**
 * 分页查询结果类
 * @Auther GuihaoLv
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {
    private Long total; //总记录数

    private List records; //当前页的数据集合
}