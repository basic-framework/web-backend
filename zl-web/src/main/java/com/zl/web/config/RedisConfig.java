package com.zl.web.config;
import com.zl.common.properties.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * @Author GuihaoLv
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {
    @Autowired
    private RedisProperties redisProperties;


    /**
     * Redis连接工厂配置
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost()); // 替换为实际的 Redis 主机名
        config.setPort(redisProperties.getPort()); // 替换为实际的 Redis 端口号
        config.setPassword(redisProperties.getPassword()); // 替换为实际的 Redis 密码
        config.setDatabase(redisProperties.getDatabase()); // 替换为实际的 Redis 数据库索引
        return new LettuceConnectionFactory(config);
    }


    /**
     * RedisTemplate配置
     * @param
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建模板对象");
        RedisTemplate redisTemplate=new RedisTemplate();
        //设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }




    /**
     * 限流脚本定义
     * @return
     */
    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText()); //加载Lua脚本
        redisScript.setResultType(Long.class); // 返回类型为Long
        return redisScript;
    }

    /**
     * 限流脚本
     */
    private String limitScriptText() {
        return "local key = KEYS[1]\n" +
                "local count = tonumber(ARGV[1])\n" +
                "local time = tonumber(ARGV[2])\n" +
                "local current = redis.call('get', key);\n" +
                "if current and tonumber(current) > count then\n" +
                "    return tonumber(current);\n" +
                "end\n" +
                "current = redis.call('incr', key)\n" +
                "if tonumber(current) == 1 then\n" +
                "    redis.call('expire', key, time)\n" +
                "end\n" +
                "return tonumber(current);";
    }

}