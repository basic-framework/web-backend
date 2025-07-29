package com.zl.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO属性Properties
 * @Author GuihaoLv
 */
@Data
@ConfigurationProperties(prefix = "zl.minio")
@Configuration
public class MinIoProperties {
    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}
