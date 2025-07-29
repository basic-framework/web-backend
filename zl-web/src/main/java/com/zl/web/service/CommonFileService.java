package com.zl.web.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

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
    }
