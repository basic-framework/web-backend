package com.zl.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT Properties
 * @Author GuihaoLv
 */
@Data
@Component
@ConfigurationProperties(prefix = "zl.token")
public class JwtProperties {
    private String secret;
    private long expireTime;
    private String header;

}