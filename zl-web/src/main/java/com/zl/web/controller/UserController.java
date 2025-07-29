package com.zl.web.controller;

import com.zl.common.result.Result;
import com.zl.common.utils.AddressUtil;
import com.zl.common.utils.IPUtil;
import com.zl.model.vo.UserNavVo;
import com.zl.web.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 用户管理接口
 * @Author GuihaoLv
 */
@RestController
@RequestMapping("/web/user")
@Slf4j
@Tag(name = "用户管理", description = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

     /**
     * 获取用户中心信息
     * @return
     */
    @GetMapping("/getUserNavInfo")
    @Operation(summary = "获取用户中心信息")
    public Result<UserNavVo> getUserNavInfo() {
        UserNavVo userNavVo = userService.getUserNavInfo();
        return Result.success(userNavVo);
    }

     /**
     * 重置密码
     * @param newPassword
     * @return
     */
    @PostMapping("/resetPassword")
    @Operation(summary = "重置密码")
    public Result<Boolean> resetPassword(@RequestParam("newPassword") String newPassword) {
        Boolean flag=userService.resetPassword(newPassword);
        return Result.success(flag);
    }


     /**
     * 更新用户信息
     * @param userNavVo
     * @return
     */
    @PutMapping("/updateUserInfo")
    @Operation(summary = "更新用户信息")
    public Result<Boolean> updateUserInfo(@RequestBody UserNavVo userNavVo) {
        Boolean flag=userService.updateUserInfo(userNavVo);
        return Result.success(flag);
    }



}
