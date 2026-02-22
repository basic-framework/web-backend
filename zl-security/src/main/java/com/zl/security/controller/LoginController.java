package com.zl.security.controller;

import com.zl.common.result.Result;
import com.zl.model.dto.EmailRegisterDto;
import com.zl.model.dto.LoginDto;
import com.zl.model.dto.SendCodeDto;
import com.zl.model.vo.LoginVo;
import com.zl.security.service.EmailService;
import com.zl.security.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * 登录管理
 * @Author GuihaoLv
 */
@RestController
@RequestMapping("/web")
@Tag(name = "登录管理", description = "登录管理")
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private EmailService emailService;


     /**
     * 用户登录
     * @param loginDto
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
        LoginVo loginVo=loginService.login(loginDto);
        return Result.success(loginVo);
    }


      /**
      * 用户退出
      * @param
      * @return
      */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<Boolean> logout() {
        Boolean flag=loginService.logout();
        return Result.success(flag);
    }

    /**
     * 发送注册验证码
     * @param sendCodeDto
     * @return
     */
    @PostMapping("/email/send-code")
    @Operation(summary = "发送注册验证码")
    public Result<String> sendEmailCode(@RequestBody SendCodeDto sendCodeDto) {
        emailService.sendRegisterCode(sendCodeDto);
        return Result.success("验证码发送成功，请查收邮件");
    }

    /**
     * 用户邮箱注册
     * @param registerDto
     * @return
     */
    @PostMapping("/email/register")
    @Operation(summary = "用户邮箱注册")
    public Result<Boolean> registerWithEmail(@RequestBody EmailRegisterDto registerDto) {
        Boolean flag = emailService.registerWithEmail(registerDto);
        return Result.success(flag);
    }




}
