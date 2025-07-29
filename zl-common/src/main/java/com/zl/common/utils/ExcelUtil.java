package com.zl.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;


 /**
 * Excel工具类(基于高性能开源组件EasyExcel)
 * @Author GuihaoLv
 */
public class ExcelUtil {

    // ============================== 导出功能 ==============================

    // 基础导出方法（修复后）
    public static <T> void excelExport(HttpServletResponse response, String fileName, String sheetName,
                                       Class<T> head, List<T> data) throws IOException {
        setResponseHeader(response, fileName);
        // 移除 .autoCloseStream(false)，使用默认自动关闭流
        EasyExcel.write(response.getOutputStream(), head)
                .sheet(sheetName)
                .doWrite(data); // 内部自动处理流关闭
    }

    // 模板导出方法（修复后）
    public static <T> void excelExportWithTemplate(HttpServletResponse response, String templatePath, String fileName,
                                                   String sheetName, Class<T> head, List<T> data) throws IOException {
        setResponseHeader(response, fileName);
        EasyExcel.write(response.getOutputStream(), head)
                .withTemplate(templatePath)
                .sheet(sheetName)
                .doWrite(data); // 移除自动流关闭配置
    }

    // 字段过滤导出方法（修复后）
    public static <T> void excelExportWithExclude(HttpServletResponse response, String fileName, String sheetName,
                                                  Class<T> head, List<T> data, Set<String> excludeColumnFiledNames) throws IOException {
        setResponseHeader(response, fileName);
        EasyExcel.write(response.getOutputStream(), head)
                .excludeColumnFiledNames(excludeColumnFiledNames)
                .sheet(sheetName)
                .doWrite(data); // 移除自动流关闭配置
    }

    // ============================== 导入功能 ==============================

    /**
     * 简单读取（同步读取，适合小文件）
     */
    public static <T> List<T> readSimple(MultipartFile file, Class<T> head) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return EasyExcel.read(is, head, null)
                    .autoCloseStream(true)
                    .doReadAllSync();
        }
    }

    /**
     * 统一响应头设置
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
        } catch (Exception e) {
            resetResponseToJson(response, "下载文件失败", e.getMessage());
        }
    }

    /**
     * 重置响应为JSON格式
     */
    private static void resetResponseToJson(HttpServletResponse response, String status, String message) {
        try {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Map<String, String> result = MapUtils.newHashMap();
            result.put("status", status);
            result.put("message", message);
            response.getWriter().println(JSON.toJSONString(result));
        } catch (IOException ex) {
            throw new RuntimeException("设置响应失败: " + ex.getMessage());
        }
    }

    // ============================== 自定义监听器（替代原ExcelListener） ==============================


    }
