package com.zl.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.zl.common.constant.UserConstant;
import com.zl.common.context.UserThreadLocal;
import com.zl.common.properties.JwtProperties;
import com.zl.common.utils.authUtils.JwtUtil;
import com.zl.model.dto.LoginDto;
import com.zl.model.vo.LoginVo;
import com.zl.model.entity.Resource;
import com.zl.model.entity.Role;
import com.zl.web.manager.security.UserAuth;
import com.zl.web.service.LoginService;
import com.zl.web.service.ResourceService;
import com.zl.web.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ResourceService resourceService;

     /**
     * 用户登录
     * @param loginDto
     * @return
     */
    public LoginVo login(LoginDto loginDto) {
        //认证用户
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                =new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //是否校验成功
        if(!authenticate.isAuthenticated()){
            throw new RuntimeException("登录失败");
        }

        //获取用户信息
        UserAuth userAuth=(UserAuth) authenticate.getPrincipal();
        //对象拷贝
        LoginVo userLoginVo= BeanUtil.copyProperties(userAuth,LoginVo.class);
        //根据用户id获取角色列表
        List<Role> roleList=roleService.getRoleListByUserId(userAuth.getId());
        Set<String> roleLabelsSet=roleList.stream().map(Role::getLabel).collect(Collectors.toSet());
        userLoginVo.setRoleLabels(roleLabelsSet);
        //获取资源列表 (请求的路径，只有类型为r才是真正的请求按钮，也就是访问路径)
        List<Resource> resourceList=resourceService.getResourceListByUserId(userAuth.getId());
        Set<String> resourceLabelSet=resourceList.stream()
                .filter(resource -> "r".equals(resource.getResourceType()))
                .map(Resource::getRequestPath)
                .collect(Collectors.toSet());
        userLoginVo.setResourcePaths(resourceLabelSet);
        //密码设置为空
        userLoginVo.setPassword("");
        //存储Redis
        String userToken= UUID.randomUUID().toString();
        userLoginVo.setToken(userToken);

        //封装JWT
        Map<String,Object> claims=new HashMap<>();
        String loginVoJson= JSONUtil.toJsonStr(userLoginVo);
        claims.put("currentUser",loginVoJson);

        String jwtToken= jwtUtil.generateToken(claims);
        String userTokenKey= UserConstant.USER_TOKEN+userLoginVo.getUsername();
        Long ttl=Long.valueOf(jwtProperties.getExpireTime()/1000);
        //存储Redis
        stringRedisTemplate.opsForValue().set(userTokenKey,userToken,ttl, TimeUnit.SECONDS);
        String jwtTokenKey=UserConstant.JWT_TOKEN+userToken;
        stringRedisTemplate.opsForValue().set(jwtTokenKey,jwtToken,ttl,TimeUnit.SECONDS);
        return userLoginVo;
    }



    /**
     * 用户退出
     * @param
     * @return
     */
    public Boolean logout() {
        String subject = UserThreadLocal.getSubject();
        LoginVo userVo = JSONObject.parseObject(subject,LoginVo.class);
        String userTokenKey=UserConstant.USER_TOKEN+userVo.getUsername();
        Boolean flag=stringRedisTemplate.delete(userTokenKey);
        if(!flag){
            throw new RuntimeException("退出登录失败");
        }
        String jwtTokenKey=UserConstant.JWT_TOKEN+userVo.getToken();
        flag=stringRedisTemplate.delete(jwtTokenKey);
        if(!flag){
            throw new RuntimeException("退出登录失败");
        }
        return flag;
    }


}
