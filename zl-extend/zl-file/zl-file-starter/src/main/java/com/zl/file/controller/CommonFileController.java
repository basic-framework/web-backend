package com.zl.file.controller;

import com.zl.common.result.Result;

import com.zl.minio.api.CommonFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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






}
