package com.zl.web.service.impl;

import com.zl.common.properties.MinIoProperties;
import com.zl.web.service.CommonFileService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class CommonFileServiceImpl implements CommonFileService {
    @Autowired
    private MinIoProperties minIoProperties;

    @Autowired
    private MinioClient client;

    /**
     * 通用文件上传请求(单个)
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {
        try {
            //判断要存储的Bucket是否存在
            boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(minIoProperties.getBucketName()).build());
            if (!bucketExists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(minIoProperties.getBucketName()).build());
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(minIoProperties.getBucketName()).config(createBucketPolicyConfig(minIoProperties.getBucketName())).build());
            }
            String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            client.putObject(PutObjectArgs.builder().
                    bucket(minIoProperties.getBucketName()).
                    object(filename).
                    stream(file.getInputStream(), file.getSize(), -1).
                    contentType(file.getContentType()).build());

            return String.join("/", minIoProperties.getEndpoint(), minIoProperties.getBucketName(), filename);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




     /**
     * 配置Bucket的访问策略
     * @param bucketName
     * @return
     */
    private String createBucketPolicyConfig(String bucketName) {
        return """
            {
              "Statement" : [ {
                "Action" : "s3:GetObject",
                "Effect" : "Allow",
                "Principal" : "*",
                "Resource" : "arn:aws:s3:::%s/*"
              } ],
              "Version" : "2012-10-17"
            }
            """.formatted(bucketName);
    }




    /**
     * 通用文件下载请求
     * @param fileName 文件名
     * @param response HttpServletResponse
     * @return 文件下载是否成功
     */

    public Boolean download(String fileName, HttpServletResponse response) {
        try {
            // 从MinIO获取文件
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(minIoProperties.getBucketName())
                    .object(fileName)
                    .build();
            InputStream inputStream = client.getObject(getObjectArgs);

            // 设置响应头，确保浏览器能够识别并下载文件
            response.setContentType("application/octet-stream"); // 设置内容类型为二进制流
            //Content-Disposition 告诉浏览器以“附件”形式下载文件，而不是尝试直接显示文件。通过指定文件名，浏览器会使用该文件名保存文件。
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // 获取输出流
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            // 将文件流写入响应流
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public byte[] download(String fileName) {
        try {
            // 从 MinIO 获取文件
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(minIoProperties.getBucketName())
                    .object(fileName)
                    .build();
            InputStream inputStream = client.getObject(getObjectArgs);

            // 将文件流转换为字节数组
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            return fileBytes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 通用文件删除请求
     * @param fileName 文件名
     * @return 删除成功返回true，否则返回false
     */
    public Boolean delete(String fileName) {
        try {
            // 删除MinIO中的文件
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(minIoProperties.getBucketName())
                    .object(fileName)
                    .build();
            client.removeObject(removeObjectArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     生成上传预签名URL（PUT）
     * @param fileName
     * @return
     */
    public String generatePresignedUploadUrl(String fileName) {
        try {
            // 安全处理文件名（防止路径遍历）
            String safeFileName = sanitizeFileName(fileName);
            // 生成预签名URL（PUT方法）
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(minIoProperties.getBucketName())
                            .object(safeFileName)
                            .expiry(15, TimeUnit.MINUTES) // 15分钟有效
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    /**
     * 生成下载预签名URL（GET）
     * @param fileName
     * @return
     */
    public String generatePresignedDownloadUrl(String fileName) {
        try {
            String safeFileName = sanitizeFileName(fileName);
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minIoProperties.getBucketName())
                            .object(safeFileName)
                            .expiry(1, TimeUnit.HOURS) // 1小时有效
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    // 文件名安全处理
    private String sanitizeFileName(String fileName) {
        // 过滤非法字符，防止路径遍历
        return fileName.replaceAll("[^a-zA-Z0-9-_.]", "");
    }
}
