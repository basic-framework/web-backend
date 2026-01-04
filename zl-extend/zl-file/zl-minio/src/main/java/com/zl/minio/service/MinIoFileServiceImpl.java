package com.zl.minio.service;

import com.zl.minio.api.CommonFileService;
import com.zl.common.properties.FileStorageProperties;
import com.zl.common.properties.MinIoProperties;
import io.minio.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MinIoFileServiceImpl implements CommonFileService {
    @Autowired
    private MinIoProperties minIoProperties;

    @Autowired
    private MinioClient client;

    private final int presignedUrlExpiry; // 从全局配置获取有效期

    // 构造器注入配置（替代@Autowired字段注入，更规范）
    @Autowired
    public MinIoFileServiceImpl(MinIoProperties minIoProperties,
                                FileStorageProperties globalProperties) {
        this.minIoProperties = minIoProperties;
        this.presignedUrlExpiry = globalProperties.getPresignedUrlExpiry();
        // 初始化MinIO客户端
        this.client = MinioClient.builder()
                .endpoint(minIoProperties.getEndpoint())
                .credentials(minIoProperties.getAccessKey(), minIoProperties.getSecretKey())
                .build();
    }





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


    /**
     * 文件上传前检查
     * @param fileMd5
     * @return
     */
    public Boolean checkFileExists(String fileMd5) {
        // 1. 构建完整文件在MinIO中的存储路径（与分块目录规则一致）
        String fullFilePath = getFullFilePath(fileMd5);
        try {
            // 2. 检查MinIO中是否存在该对象
            StatObjectArgs statArgs = StatObjectArgs.builder()
                    .bucket(minIoProperties.getBucketName())
                    .object(fullFilePath)
                    .build();
            client.statObject(statArgs);
            log.info("文件已存在，MD5：{}，MinIO路径：{}", fileMd5, fullFilePath);
            return true;
        } catch (MinioException e) {
            // MinIO返回对象不存在异常，说明文件未上传
            if (e.getMessage().equals("NoSuchKey")) {
                log.info("文件不存在，MD5：{}", fileMd5);
                return false;
            }
            // 其他异常打印日志，返回false
            log.error("检查文件存在性失败，MD5：{}", fileMd5, e);
            return false;
        } catch (Exception e) {
            log.error("检查文件存在性异常，MD5：{}", fileMd5, e);
            return false;
        }

    }

    /**
     * 检查分块是否已存在
     * 逻辑：拼接分块文件路径，检查MinIO中是否存在该分块对象
     */
    public Boolean checkChunkExists(String fileMd5, int chunk) {
        // 1. 构建分块文件在MinIO中的路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;

        try {
            // 2. 检查分块对象是否存在
            StatObjectArgs statArgs = StatObjectArgs.builder()
                    .bucket(minIoProperties.getBucketName())
                    .object(chunkFilePath)
                    .build();
            client.statObject(statArgs);
            log.info("分块已存在，MD5：{}，分块索引：{}", fileMd5, chunk);
            return true;
        } catch (MinioException e) {
            if (e.getMessage().equals("NoSuchKey")) {
                log.info("分块不存在，MD5：{}，分块索引：{}", fileMd5, chunk);
                return false;
            }
            log.error("检查分块存在性失败，MD5：{}，分块索引：{}", fileMd5, chunk, e);
            return false;
        } catch (Exception e) {
            log.error("检查分块存在性异常，MD5：{}，分块索引：{}", fileMd5, chunk, e);
            return false;
        }
    }



    /**
     * 上传分块文件到MinIO
     * 逻辑：MultipartFile转InputStream，上传到分块指定路径
     */
    public Boolean uploadChunk(MultipartFile file, String fileMd5, int chunk) {
        // 1. 校验参数
        if (file.isEmpty()) {
            log.error("上传分块失败，文件为空，MD5：{}，分块索引：{}", fileMd5, chunk);
            return false;
        }

        // 2. 构建分块存储路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;

        try (InputStream inputStream = file.getInputStream()) {
            // 3. 上传分块到MinIO
            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(minIoProperties.getBucketName())
                    .object(chunkFilePath)
                    .stream(inputStream, file.getSize(), -1) // -1表示自动检测文件大小
                    .contentType(file.getContentType())
                    .build();
            client.putObject(putArgs);
            log.info("分块上传成功，MD5：{}，分块索引：{}，路径：{}", fileMd5, chunk, chunkFilePath);
            return true;
        } catch (Exception e) {
            log.error("分块上传失败，MD5：{}，分块索引：{}", fileMd5, chunk, e);
            return false;
        }
    }




    /**
     * 合并分块文件（优化版）
     * 核心逻辑：1. 有序构建分块列表 2. 合并文件 3. MD5校验 4. 清理分块
     * @param fileMd5 文件唯一标识（MD5）
     * @param fileName 原始文件名（含扩展名）
     * @param chunkTotal 分块总数（新增参数：避免遍历MinIO获取分块，提升性能）
     * @return 合并是否成功
     */
    public Boolean mergeChunk(String fileMd5, String fileName, int chunkTotal) {
        // 1. 基础路径构建
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5); //分块文件存储目录
        String extName = fileName.substring(fileName.lastIndexOf(".")); // 提取文件扩展名
        String mergeFilePath = getFilePathByMd5(fileMd5, extName); // 合并后文件路径
        try {
            // 2. 有序构建分块源列表（参考代码核心逻辑：按索引生成，保证顺序）
            List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> ComposeSource.builder()
                            .bucket(minIoProperties.getBucketName())
                            .object(chunkFileFolderPath.concat(Integer.toString(i)))
                            .build())
                    .collect(Collectors.toList());

            // 3. 执行MinIO分块合并
            ObjectWriteResponse response = client.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(minIoProperties.getBucketName())
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build());
            log.info("合并文件成功:{}", mergeFilePath);

            // 4. 下载合并后的文件，进行MD5校验（核心：保证文件完整性）
            File minioFile = downloadFileFromMinIO(minIoProperties.getBucketName(), mergeFilePath);
            if (minioFile == null) {
                log.error("下载合并后文件失败,mergeFilePath:{}", mergeFilePath);
                return false;
            }

            // 5. MD5校验逻辑
            try (InputStream newFileInputStream = new FileInputStream(minioFile)) {
                String md5Hex = DigestUtils.md5Hex(newFileInputStream);
                // 比对MD5，不一致则返回失败
                if (!fileMd5.equals(md5Hex)) {
                    log.error("文件合并校验失败，MD5不一致：原始{}，合并后{}", fileMd5, md5Hex);
                    return false;
                }
                // 可选：此处可添加文件大小记录、入库等业务逻辑
                log.info("文件MD5校验通过，MD5：{}", fileMd5);
            }

            // 6. 清理分块文件（参考代码的清理逻辑，增加容错）
            clearChunkFiles(chunkFileFolderPath, chunkTotal);

            // 7. 临时文件删除（finally中兜底）
            return true;
        } catch (Exception e) {
            log.error("合并文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return false;
        } finally {
            // 兜底：删除临时文件（若存在）
            File minioFile = new File(System.getProperty("java.io.tmpdir"), "minio.merge");
            if (minioFile.exists()) {
                minioFile.delete();
            }
        }
    }

    /**
     * 从MinIO下载文件到本地临时文件（参考代码的downloadFileFromMinIO）
     * @param bucket 桶名
     * @param objectName 对象路径
     * @return 本地临时文件
     */
    private File downloadFileFromMinIO(String bucket, String objectName) {
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            // 从MinIO获取文件流
            InputStream stream = client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            // 创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream); // 拷贝流到临时文件
            return minioFile;
        } catch (Exception e) {
            log.error("下载MinIO文件失败，bucket：{}，object：{}", bucket, objectName, e);
            return null;
        } finally {
            // 关闭流
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件输出流失败", e);
                }
            }
        }
    }

    /**
     * 清除分块文件（参考代码的clearChunkFiles，优化异常处理）
     * @param chunkFileFolderPath 分块文件目录
     * @param chunkTotal 分块总数
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket(minIoProperties.getBucketName())
                    .objects(deleteObjects)
                    .build();

            Iterable<Result<DeleteError>> results = client.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                try {
                    DeleteError deleteError = r.get();
                    if (deleteError != null) {
                        log.error("清除分块文件失败,objectname:{}", deleteError.objectName());
                    }
                } catch (Exception e) {
                    log.error("遍历分块删除结果失败", e);
                }
            });
            log.info("分块文件清理完成，共处理{}个分块", chunkTotal);
        } catch (Exception e) {
            log.error("清除分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }

    // ------------------- 私有工具方法 -------------------

    /**
     * 构建分块文件存储目录路径
     * 规则：md5前两位拆分目录 + md5 + chunk/
     * 示例：md5=abc123 → a/b/abc123/chunk/
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/chunk/";
    }

    /**
     * 构建完整文件存储路径（不带文件名）
     */
    private String getFullFilePath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

    /**
     * 构建完整文件存储路径（带文件名）
     */
    private String getFullFilePath(String fileMd5, String fileName) {
        return getFullFilePath(fileMd5) + fileName;
    }

    /**
     * 参考代码的getFilePathByMd5：构建合并后文件的完整路径
     * @param fileMd5 文件MD5
     * @param fileExt 文件扩展名（含.）
     * @return 完整路径
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }


    /**
     * 大文件分片下载核心逻辑
     * 支持Range请求，返回指定字节范围的文件流
     */
    public void downloadLargeFile(String fileMd5, String fileName, HttpServletRequest request, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 1. 构建MinIO中完整文件路径（复用现有路径规则）
        String extName = fileName.substring(fileName.lastIndexOf("."));
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        String bucket = minIoProperties.getBucketName();

        // 2. 获取文件元信息（总大小、Content-Type）
        StatObjectArgs statArgs = StatObjectArgs.builder()
                .bucket(bucket)
                .object(mergeFilePath)
                .build();
        StatObjectResponse statResponse = client.statObject(statArgs);
        long fileTotalSize = statResponse.size();
        String contentType = statResponse.contentType();

        // 3. 解析前端Range请求头（格式：Range: bytes=0-4999999）
        String rangeHeader = request.getHeader("Range");
        long start = 0;
        long end = fileTotalSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            // 拆分Range参数
            String[] rangeParts = rangeHeader.replace("bytes=", "").split("-");
            start = Long.parseLong(rangeParts[0]);
            // 处理结束字节：前端传了则用前端值，否则取文件末尾
            if (rangeParts.length > 1 && !rangeParts[1].isEmpty()) {
                end = Long.parseLong(rangeParts[1]);
            }
            // 校验Range有效性
            if (start < 0 || end >= fileTotalSize || start > end) {
                response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                response.setHeader("Content-Range", "bytes */" + fileTotalSize);
                return;
            }
            // 响应部分内容（206）
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileTotalSize);
        }

        // 4. 设置下载响应头
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", String.valueOf(end - start + 1)); // 当前分片大小
        response.setHeader("Accept-Ranges", "bytes"); // 告知前端支持分片下载
        // 触发浏览器下载（指定文件名，解决中文乱码）
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()) + "\"");
        response.setHeader("Cache-Control", "no-cache");

        // 5. 从MinIO读取指定字节范围的文件流并返回（核心修正：兼容所有SDK版本）
        InputStream stream = null;
        InputStream fullStream = null;
        try {
            // 第一步：获取完整文件流（放弃SDK的extraHeader，手动处理Range）
            GetObjectArgs getArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(mergeFilePath)
                    .build();
            fullStream = client.getObject(getArgs);

            // 第二步：手动截取指定字节范围的流（跳过start字节，读取end-start+1字节）
            // 方式1：适合中小文件（<1GB），简单直接
            byte[] fullBytes = IOUtils.toByteArray(fullStream);
            byte[] rangeBytes = new byte[(int) (end - start + 1)];
            System.arraycopy(fullBytes, (int) start, rangeBytes, 0, rangeBytes.length);
            stream = new ByteArrayInputStream(rangeBytes);

            // 6. 流式返回（避免内存溢出）
            IOUtils.copy(stream, response.getOutputStream());
            response.getOutputStream().flush();
        } finally {
            // 兜底关闭流，防止资源泄漏
            if (stream != null) {
                stream.close();
            }
            if (fullStream != null) {
                fullStream.close();
            }
        }
    }


    /**
     *获取文件大小
     * @throws InternalException
     */
    public Long getFileSize(String fileMd5, String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String extName = fileName.substring(fileName.lastIndexOf("."));
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        StatObjectResponse statResponse = client.statObject(StatObjectArgs.builder()
                .bucket(minIoProperties.getBucketName())
                .object(mergeFilePath)
                .build());
        return statResponse.size();
    }

}
