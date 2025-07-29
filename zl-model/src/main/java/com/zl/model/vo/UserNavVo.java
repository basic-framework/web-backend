package com.zl.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
 * 用户中心信息
 * @Auther: GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserNavVo {
   private Long userId;
   private String username;
   private String avatar;
   private Long roleId;
   private String email;
   private String phoneNumber;
}
