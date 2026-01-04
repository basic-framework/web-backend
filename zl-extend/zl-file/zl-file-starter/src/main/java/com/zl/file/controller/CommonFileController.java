package com.zl.file.controller;

import com.zl.common.result.Result;

import com.zl.minio.api.CommonFileService;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.rmi.ServerException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

 /**
 * 通用文件接口
 * @author GuihaoLv
 */
@RestController
@RequestMapping("/web/commonFile")
@Slf4j
@Tag(name = "通用文件接口",description = "通用文件接口")
public class CommonFileController {
    @Autowired
    private CommonFileService commonFileService;

    /**
     * 通用文件上传请求(单个)
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @Operation(summary = "通用文件上传请求(单个)")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file){
        String url=commonFileService.upload(file);
        return Result.success(url);
    }


    /**
     * 通用文件上传请求(多个)
     * @param files
     * @return
     */
    @PostMapping("/uploads")
    @Operation(summary = "通用文件上传请求(多个)")
    public Result<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        // 用于保存每个文件的上传结果
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            // 调用单文件上传逻辑
            String url = commonFileService.upload(file);
            if (url != null) {
                urls.add(url);
            } else {
                throw new RuntimeException("文件上传失败：" + file.getOriginalFilename());
            }
        }
        return Result.success(urls);
    }



    /**
     * 通用文件下载请求
     * @param fileName
     * @return
     */
    @PostMapping("/download")
    @Operation(summary = "通用文件下载请求")
    public Result downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response){
        boolean success=commonFileService.download(fileName,response);
        if (success) {
            return Result.success();
        } else {
            return Result.fail("文件下载失败");
        }
    }


    /**
     * 通用文件删除请求
     * @param fileName 文件名
     * @return
     */
    @DeleteMapping("/delete")
    @Operation(summary = "通用文件删除请求")
    public Result<String> deleteFile(@RequestParam("fileName") String fileName) {
        boolean isDeleted = commonFileService.delete(fileName);
        if (isDeleted) {
            return Result.success("文件删除成功");
        } else {
            return Result.fail("文件删除失败");
        }
    }



    /**
    * 生成上传预签名URL（PUT）
    * @param fileName
    * @return
    * 要改成使用预签名URL，让前端直接与MinIO交互，减轻服务器负担。
    */
    @GetMapping("/presigned-upload-url")
    @Operation(summary = "生成上传预签名URL（PUT）")
    public Result<String> generateUploadUrl(@RequestParam("fileName") String fileName) {
        String url = commonFileService.generatePresignedUploadUrl(fileName);
        return Result.success(url);
    }

     /**
     * 生成下载预签名URL（GET）
     * @param fileName
     * @return
     * 要改成使用预签名URL，让前端直接与MinIO交互，减轻服务器负担。
     */
    @GetMapping("/presigned-download-url")
    @Operation(summary = "生成下载预签名URL（GET）")
    public Result<String> generateDownloadUrl(@RequestParam("fileName") String fileName) {
        String url = commonFileService.generatePresignedDownloadUrl(fileName);
        return Result.success(url);
    }



     /**
      * 文件上传前检查
      * @param fileMd5
      * @return
      */
     @PostMapping("/upload/checkFile")
     @Operation(summary = "文件上传前检查")
     public Result<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
         Boolean exists = commonFileService.checkFileExists(fileMd5);
         return Result.success(exists);
     }

     /**
      * 分块上传前检查
      * @param fileMd5
      * @param chunk
      * @return
      */
     @PostMapping("/upload/checkChunk")
     @Operation(summary = "文件上传前检查")
     public Result<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                       @RequestParam("chunk")int chunk) {
         Boolean exists = commonFileService.checkChunkExists(fileMd5,chunk);
         return Result.success(exists);
     }

     /**
      * 上传分块文件
      * @param fileMd5
      * @param chunk
      * @return
      */
     @PostMapping("/upload/uploadChunk")
     @Operation(summary = "传分块文件")
     public Result<Boolean> uploadChunk(@RequestParam("file") MultipartFile file,
                                        @RequestParam("fileMd5") String fileMd5,
                                        @RequestParam("chunk")int chunk) {
         Boolean success = commonFileService.uploadChunk(file, fileMd5, chunk);
         return Result.success(success);
     }

     /**
      * 合并分块文件
      * @param fileMd5
      * @param chunkTotal
      * @return
      */
     @PostMapping("/upload/mergeChunk")
     @Operation(summary = "合并分块文件")
     public Result<Boolean> mergeChunk(@RequestParam("fileMd5") String fileMd5,
                                       @RequestParam("fileName") String fileName,
                                       @RequestParam("chunkTotal") int chunkTotal) {
         Boolean success = commonFileService.mergeChunk(fileMd5, fileName, chunkTotal);
         return Result.success(success);
     }

     /**
      * 大文件分片下载接口（支持Range字节范围请求）
      * @param fileMd5 文件MD5（定位MinIO文件）
      * @param fileName 文件名（含扩展名，用于拼接路径）
      * @param request 获取Range请求头
      * @param response 返回分片流+响应头
      */
     @PostMapping("/download/largeFile")
     @Operation(summary = "大文件分片下载")
     public void downloadLargeFile(@RequestParam("fileMd5") String fileMd5,
                                   @RequestParam("fileName") String fileName,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
         try {
             // 调用服务层分片下载逻辑
             commonFileService.downloadLargeFile(fileMd5, fileName, request, response);
         } catch (Exception e) {
             log.error("大文件分片下载失败，fileMd5：{}", fileMd5, e);
             response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         }
     }

     /**
      * 辅助接口：获取文件总大小（前端初始化分片下载时调用）
      */
     @PostMapping("/download/getFileSize")
     @Operation(summary = "获取文件总大小")
     public Result<Long> getFileSize(@RequestParam("fileMd5") String fileMd5,
                                     @RequestParam("fileName") String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
         Long res=commonFileService.getFileSize(fileMd5, fileName);
         return Result.success(res);
     }




 }
