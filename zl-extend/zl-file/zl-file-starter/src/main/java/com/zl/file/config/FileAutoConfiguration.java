package com.zl.file.config;

import com.zl.file.controller.CommonFileController;
import com.zl.common.properties.FileStorageProperties;
import com.zl.common.properties.MinIoProperties;
import com.zl.minio.api.CommonFileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        FileStorageProperties.class,
        MinIoProperties.class
})
@ConditionalOnClass(CommonFileService.class) // 接口存在时生效
public class FileAutoConfiguration {

    /**
     * 注入MinIO实现（当配置type=minio时）
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "file.storage",
            name = "type",
            havingValue = "minio",
            matchIfMissing = false
    )
    @ConditionalOnMissingBean // 允许用户自定义实现覆盖
    public CommonFileService minioFileService(MinIoProperties minIoProperties,
                                              FileStorageProperties globalProperties) {
        // 传入全局配置（如预签名URL有效期）
        return new com.zl.minio.service.MinIoFileServiceImpl(minIoProperties, globalProperties);
    }

    /**
     * 注入内置Controller（当启用且存在CommonFileService实现时）
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "file.storage",
            name = "controllerEnabled",
            havingValue = "true",
            matchIfMissing = true
    )
    @ConditionalOnMissingBean // 允许用户自定义Controller覆盖
    public CommonFileController commonFileController(CommonFileService fileService) {
        return new CommonFileController(); // 依赖Spring自动注入fileService
    }
}