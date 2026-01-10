package com.zl.common.utils.commonUtils;

import com.zl.common.utils.springUtils.SpringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 获取i18n资源文件
 * 根据指定的文案编码（code）和参数，获取对应语言环境下的国际化文案
 * @Author: GuihaoLv
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtils {

    private static final MessageSource MESSAGE_SOURCE = SpringUtil.getBean(MessageSource.class);

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args) {
        return MESSAGE_SOURCE.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
