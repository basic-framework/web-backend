package com.zl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
 * 重置密码DTO
 * @Author: GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordDto {
    private String email;
    private String newPassword;
    private String verifyCode;
}
