package com.zl.common.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * 全局文件存储配置属性
 * 对应配置文件中的 "file.storage" 前缀
 */
@Data
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {

    /**
     * 存储类型：minio/aliyun，必填
     */
    private String type;

    /**
     * Controller接口路径前缀，默认 "/web/commonFile"
     */
    private String controllerPath = "/web/commonFile";

    /**
     * 是否启用内置Controller，默认true
     */
    private boolean controllerEnabled = true;

    /**
     * 预签名URL默认有效期（分钟），默认15分钟
     */
    private int presignedUrlExpiry = 15;
}
