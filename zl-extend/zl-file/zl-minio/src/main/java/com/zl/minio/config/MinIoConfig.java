package com.zl.minio.config;

import com.zl.common.properties.MinIoProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


 /**
 * MinIO配置类
 * @Author GuihaoLv
 */
@Configuration
@EnableConfigurationProperties(MinIoProperties.class)
public class MinIoConfig{
    @Autowired
    private MinIoProperties minIoProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minIoProperties.getEndpoint())
                .credentials(minIoProperties.getAccessKey(), minIoProperties.getSecretKey())
                .build();
    }



}
