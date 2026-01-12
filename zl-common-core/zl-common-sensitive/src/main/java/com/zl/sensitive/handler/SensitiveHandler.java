package com.zl.sensitive.handler;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.zl.common.utils.springUtils.SpringUtil;
import com.zl.sensitive.annotation.Sensitive;
import com.zl.sensitive.core.SensitiveService;
import com.zl.sensitive.core.SensitiveStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import java.io.IOException;
import java.util.Objects;

/**
 * 数据脱敏json序列化工具
 * 只在Jackson 序列化字段时执行
 * 仅对 “加了 @Sensitive 注解的 String 字段” 执行
 *
 * @Author GuihaoLv
 */
@Slf4j
public class SensitiveHandler extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveStrategy strategy;

    /**
     * 执行脱敏逻辑（核心）
     * @param value
     * @param gen
     * @param serializers
     * @throws IOException
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            // 1. 获取业务侧实现的脱敏开关（SensitiveService）
            SensitiveService sensitiveService = SpringUtil.getBean(SensitiveService.class);
            // 2. 业务侧要求脱敏 → 执行指定策略的脱敏逻辑，输出脱敏后的值
            if (ObjectUtil.isNotNull(sensitiveService) && sensitiveService.isSensitive()) {
                gen.writeString(strategy.desensitizer().apply(value));
            } else {
                // 3. 不脱敏 → 输出原始值（比如管理员查看时）
                gen.writeString(value);
            }
        } catch (BeansException e) {
            // 4. 异常兜底（如没实现SensitiveService）→ 输出原始值，避免系统报错
            log.error("脱敏实现不存在, 采用默认处理 => {}", e.getMessage());
            gen.writeString(value);
        }
    }

    /**
     * 解析注解，初始化脱敏策略
     * 加了 @Sensitive 注解的 String 字段” 生效，精准定位需要脱敏的字段
     * @param prov
     * @param property
     * @return
     * @throws JsonMappingException
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        // 1. 获取字段上的@Sensitive注解
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        // 2. 校验：注解存在 + 字段类型是String → 初始化脱敏策略
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.strategy = annotation.strategy(); // 保存当前字段要使用的脱敏策略（如PHONE/ID_CARD）
            return this; // 返回当前处理器，后续序列化该字段时用本类的逻辑
        }
        // 3. 无注解/非String字段 → 用Jackson默认的序列化器（不脱敏）
        return prov.findValueSerializer(property.getType(), property);
    }
}
