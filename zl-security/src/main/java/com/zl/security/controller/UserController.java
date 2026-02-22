package com.zl.security.controller;
import com.zl.common.result.Result;
import com.zl.model.dto.ResetPasswordDto;
import com.zl.model.dto.SendCodeDto;
import com.zl.model.vo.UserNavVo;
import com.zl.security.service.EmailService;
import com.zl.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private EmailService emailService;

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
     * 发送密码修改验证码
     * @param sendCodeDto
     * @return
     */
    @PostMapping("/email/resetPw/code")
    @Operation(summary = "发送密码修改验证码")
    public Result<String> sendEmailCode(@RequestBody SendCodeDto sendCodeDto) {
        emailService.sendResetPwCode(sendCodeDto);
        return Result.success("验证码发送成功，请查收邮件");
    }

     /**
     * 修改密码
     * @param resetPasswordDto
     * @return
     */
    @PostMapping("/resetPassword")
    @Operation(summary = "修改密码")
    public Result<Boolean> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        Boolean flag=userService.resetPassword(resetPasswordDto);
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
