package com.zl.generator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 代码生成器配置类
 *
 * @author code-generator
 * @date 2026-01-23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "gen")
@PropertySource(value = "classpath:generator.yml", factory = YamlPropertySourceFactory.class)
public class GenConfig {

    /**
     * 作者名称
     */
    private String author = "code-generator";

    /**
     * 生成代码的包路径
     */
    private String packageName = "com.zl";

    /**
     * 模块名称
     */
    private String moduleName = "system";

    /**
     * 自动去除表前缀
     */
    private Boolean autoRemovePre = false;

    /**
     * 表前缀（多个用逗号分隔）
     */
    private String tablePrefix = "";

    /**
     * 输出路径（默认为项目根目录下的 src/main/java）
     */
    private String outputDir = System.getProperty("user.dir") + "/zl-web/src/main/java";

    /**
     * XML输出路径
     */
    private String xmlOutputDir = System.getProperty("user.dir") + "/zl-web/src/main/resources/mapper";
}
