package com.zl.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

 /**
 * 阿里云配置属性
 * @Author GuihaoLv
 */
@Data
@Component
@ConfigurationProperties(prefix = "zl.aliyun")
public class AliyunProperties {
    private String accessKey;
    private String secretKey;
}