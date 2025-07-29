package com.zl.web.manager.springai.model;
import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 会话表实体类
 * @Author GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Session extends BaseEntity implements Serializable {
    private String sessionName; //会话名称
}