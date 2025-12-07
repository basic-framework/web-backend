package com.zl.framework.config;

import com.zl.common.properties.RedisProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson分布式锁配置类
 * @Author GuihaoLv
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        String redisAddress = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        config.useSingleServer().setAddress(redisAddress);
        
        // 设置密码（如果存在）
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            config.useSingleServer().setPassword(redisProperties.getPassword());
        }
        
        // 设置数据库
        config.useSingleServer().setDatabase(redisProperties.getDatabase());
        
        // 创建RedissonClient对象
        return Redisson.create(config);
    }




}
