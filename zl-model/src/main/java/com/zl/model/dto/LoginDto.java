package com.zl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
 * 登录dto
 * @Author GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {
    private String username;
    private String password;
}
