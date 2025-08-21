package com.zl.model.vo;

import com.zl.model.entity.security.User;
import lombok.*;
import java.util.Set;

 /**
 * B端用户登录vo
 * @Author GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo extends User {
    private String token; //用户token
    //private Set<String> roleIds; //用户角色id集合
    private Set<String> roleLabels; //用户角色标识集合
    //  private List<Role> roleList; //用户角色集合
    private Set<String> resourcePaths; //用户资源路径集合
}