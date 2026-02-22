package com.zl.security.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.zl.common.constant.UserConstant;
import com.zl.model.dto.EmailRegisterDto;
import com.zl.model.dto.SendCodeDto;
import com.zl.model.entity.security.User;
import com.zl.security.mapper.UserMapper;
import com.zl.security.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 邮件服务实现
 * @Author GuihaoLv
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Redis key 前缀
    private static final String EMAIL_CODE_PREFIX = "email:code:";
    private static final String EMAIL_LIMIT_PREFIX = "email:limit:";

    // 验证码有效期（分钟）
    private static final long CODE_EXPIRE_TIME = 5;

    // 发送限制（秒）
    private static final long SEND_LIMIT_TIME = 60;

    /**
     * 发送注册验证码
     */
    @Override
    public void sendRegisterCode(SendCodeDto sendCodeDto) {
        String email = sendCodeDto.getEmail();

        // 检查发送频率限制
        String limitKey = EMAIL_LIMIT_PREFIX + email;
        Boolean hasLimit = stringRedisTemplate.hasKey(limitKey);
        if (Boolean.TRUE.equals(hasLimit)) {
            Long ttl = stringRedisTemplate.getExpire(limitKey, TimeUnit.SECONDS);
            throw new RuntimeException("发送过于频繁，请" + ttl + "秒后重试");
        }

        // 检查邮箱是否已注册
        User existUser = userMapper.findUserByEmail(email);
        if (existUser != null) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 存储验证码到Redis
        String codeKey = EMAIL_CODE_PREFIX + email;
        stringRedisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_TIME, TimeUnit.MINUTES);

        // 设置发送限制
        stringRedisTemplate.opsForValue().set(limitKey, "1", SEND_LIMIT_TIME, TimeUnit.SECONDS);

        // 发送邮件
        try {
            sendEmail(email, "注册验证码", "您的注册验证码是：<strong>" + code + "</strong>，5分钟内有效。");
            log.info("验证码发送成功，邮箱：{}", email);
        } catch (Exception e) {
            // 发送失败，删除已存储的验证码
            stringRedisTemplate.delete(codeKey);
            stringRedisTemplate.delete(limitKey);
            log.error("邮件发送失败：{}", e.getMessage());
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }
    }

    /**
     * 邮箱注册
     */
    @Override
    @Transactional
    public Boolean registerWithEmail(EmailRegisterDto registerDto) {
        String email = registerDto.getEmail();
        String username = registerDto.getUsername();
        String password = registerDto.getPassword();

        String verifyCode = registerDto.getVerifyCode();

        // 验证验证码
        String codeKey = EMAIL_CODE_PREFIX + email;
        String savedCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (savedCode == null) {
            throw new RuntimeException("验证码已过期或不存在");
        }
        if (!savedCode.equals(verifyCode)) {
            throw new RuntimeException("验证码错误");
        }

        // 检查用户名是否已存在
        User existUser = userMapper.findUserVoForLogin(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 再次检查邮箱是否已注册（防止并发）
        existUser = userMapper.findUserByEmail(email);
        if (existUser != null) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 创建用户
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .build();

        int count= userMapper.insert(newUser);
        Boolean result=false;
        if(count!=0){
            result=true;
        }

        // 注册成功后删除验证码
        if (Boolean.TRUE.equals(result)) {
            stringRedisTemplate.delete(codeKey);
            log.info("用户注册成功，用户名：{}，邮箱：{}", username, email);
        }

        return result;
    }

    /**
     * 密码重置验证码
     * @param sendCodeDto
     */
    public void sendResetPwCode(SendCodeDto sendCodeDto) {
        String email = sendCodeDto.getEmail();

        // 检查发送频率限制
        String limitKey = EMAIL_LIMIT_PREFIX + email;
        Boolean hasLimit = stringRedisTemplate.hasKey(limitKey);
        if (Boolean.TRUE.equals(hasLimit)) {
            Long ttl = stringRedisTemplate.getExpire(limitKey, TimeUnit.SECONDS);
            throw new RuntimeException("发送过于频繁，请" + ttl + "秒后重试");
        }

        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 存储验证码到Redis
        String codeKey = EMAIL_CODE_PREFIX + email;
        stringRedisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_TIME, TimeUnit.MINUTES);

        // 设置发送限制
        stringRedisTemplate.opsForValue().set(limitKey, "1", SEND_LIMIT_TIME, TimeUnit.SECONDS);

        // 发送邮件
        try {
            sendEmail(email, "密码修改验证码", "您的密码修改验证码是：<strong>" + code + "</strong>，5分钟内有效。");
            log.info("验证码发送成功，邮箱：{}", email);
        } catch (Exception e) {
            // 发送失败，删除已存储的验证码
            stringRedisTemplate.delete(codeKey);
            stringRedisTemplate.delete(limitKey);
            log.error("邮件发送失败：{}", e.getMessage());
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }
    }

    /**
     * 发送HTML邮件
     */
    private void sendEmail(String to, String subject, String content) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText("<html><body>" + content + "</body></html>", true);
        mailSender.send(message);
    }
}
