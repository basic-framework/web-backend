package com.zl.common.utils.authUtils;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.zl.common.context.UserThreadLocal;
import com.zl.model.vo.LoginVo;
import com.zl.model.entity.security.User;


 /**
 * 用户工具类
 * @Auther GuihaoLv
 */
public class UserUtil {



    /**
    * 获取当前登录用户信息
    * @return
    */
    public static User getUser() {
        String userSubject = UserThreadLocal.getSubject();
        if (StrUtil.isEmpty(userSubject)) {
            throw new RuntimeException("无法获取当前用户");
        }
        try {
            return JSONObject.parseObject(userSubject, LoginVo.class);
        } catch (Exception e) {
            throw new RuntimeException("无法获取当前用户");
        }
    }



     /**
     * 获取当前登录用户ID
     * @return
     */
    public static Long getUserId() {
        User user = getUser();
        return user.getId();
    }


}
