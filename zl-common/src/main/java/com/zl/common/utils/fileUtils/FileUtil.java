package com.zl.common.utils.fileUtils;

import com.zl.common.properties.MinIoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件工具类
 * @Author GuihaoLv
 */
@EnableConfigurationProperties(MinIoProperties.class)
@Component
public class FileUtil {
    @Autowired
    private MinIoProperties minIoProperties;

     /**
     * 从URL中提取文件名
     * @param url
     * @return
     */
    public  String extractFileNameFromUrl(String url) {
        String bucketName = minIoProperties.getBucketName(); // nursing-house
        // 找到bucketName在url中的位置
        int idx = url.indexOf(bucketName);
        if (idx == -1) {
            // 没找到bucketName，直接返回null或者抛异常
            return null;
        }
        // 文件名从bucketName后面开始截取（包括bucketName本身后面的斜杠）
        // bucketName长度 + 1是跳过斜杠
        return url.substring(idx + bucketName.length() + 1);
    }
}
