package com.zl.web.mapper;


import com.zl.model.entity.security.User;
import com.zl.model.vo.UserNavVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from sys_user where username=#{username}")
    User findUserVoForLogin(String username);

    /**
     * 获取用户中心信息
     * @return
     */
    @Select("select u.username,u.avatar,u.phone_number,u.email,ur.role_id from sys_user u left join  sys_user_role ur on u.id=ur.user_id " +
            "where u.id=#{id}")
    UserNavVo getUserNavInfo(Long id);

    /**
     * 重置密码
     * @param userId
     * @param encodedPassword
     * @return
     */
    @Update("UPDATE sys_user SET password = #{encodedPassword} WHERE id=#{userId}")
    Boolean updatePasswordByUsername(Long userId, String encodedPassword);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    Boolean updateUserInfo(User user);

    /**
     * 插入用户
     * @param user
     */
    Boolean insert(User user);

    /**
     * 根据用户名查询id
     * @param username
     * @return
     */
    @Select("select id from sys_user where username=#{username}")
    Long selectIdByUsername(String username);

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @Delete("delete from sys_user where id=#{userId}")
    Boolean deleteById(Long userId);



}
