package com.zl.mcpServer.tool;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ImageSearchToolTest {
    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void searchImage() {
        // 1. 构造合法的JSON字符串（将舞蹈描述封装为JSON对象）
        String jsonParam = "{\n" +
                "  \"description\":女孩";

        // 2. 调用工具方法
        String result = imageSearchTool.searchImage(jsonParam);

        // 3. 打印结果
        System.out.printf("result=%s\n", result);
    }


}