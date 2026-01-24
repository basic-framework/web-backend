package com.zl.generator.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Properties;

/**
 * Yaml PropertySource Factory
 * 用于支持 @PropertySource 加载 YAML 配置文件
 *
 * @author code-generator
 * @date 2026-01-23
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource.getResource());

        Properties properties = factoryBean.getObject();

        return new PropertiesPropertySource(
                resource.getResource().getFilename(),
                properties
        );
    }
}
