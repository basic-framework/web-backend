package com.zl.idempotent.config;

import com.zl.idempotent.aspectj.RepeatSubmitAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 幂等功能配置
 * @Author GuihaoLv
 */
@AutoConfiguration
public class IdempotentConfig {

    @Bean
    public RepeatSubmitAspect repeatSubmitAspect() {
        return new RepeatSubmitAspect();
    }

}
