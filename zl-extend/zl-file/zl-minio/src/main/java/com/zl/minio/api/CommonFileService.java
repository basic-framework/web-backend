package com.zl.minio.api;

import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface CommonFileService {

    /**
     * 通用文件上传请求(单个)
     * @param file
     * @return
     */
    String upload(MultipartFile file);


    /**
     * 通用文件下载请求
     *
     * @param fileName
     * @param response
     * @return
     */
    Boolean download(String fileName, HttpServletResponse response);

    /**
     * 通用文件删除请求
     * @param fileName
     * @return
     */
    Boolean delete(String fileName);

    /**
     * 生成上传预签名URL（PUT）
     * @param fileName
     * @return
     */
    String generatePresignedUploadUrl(String fileName);


    /**
     * 生成下载预签名URL（GET）
     * @param fileName
     * @return
     */
    String generatePresignedDownloadUrl(String fileName);

    /**
     * 文件上传前检查
     * @param fileMd5
     * @return
     */
    Boolean checkFileExists(String fileMd5);

    /**
     * 检查分块是否存在
     * @param fileMd5
     * @param chunk
     * @return
     */
    Boolean checkChunkExists(String fileMd5, int chunk);

    /**
     * 检查分块是否存在
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     */
    Boolean uploadChunk(MultipartFile file, String fileMd5, int chunk);

    /**
     * 合并分块
     * @param fileMd5
     * @param fileName
     * @return
     */
    Boolean mergeChunk(String fileMd5, String fileName, int chunkTotal);

    /**
     * 下载大文件
     * @param fileMd5
     * @param fileName
     * @param request
     * @param response
     */
    void downloadLargeFile(String fileMd5, String fileName, HttpServletRequest request, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     * 获取文件大小
     * @param fileMd5
     * @param fileName
     */
    Long getFileSize(String fileMd5, String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;


}
