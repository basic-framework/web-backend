package com.zl.model.enums;

import lombok.Getter;

/**
 * 基础枚举接口
 */
public interface IBasicEnum {
    /**
     * 比较枚举值与目标值是否一致
     * @param value 目标值
     * @return 一致返回true，否则返回false
     */
    default boolean equalsValue(Integer value) {
        if (value == null) {
            return false;
        }
        return this.getValue() == value.intValue();
    }

    /**
     * 获取枚举值
     * @return 枚举对应的数字值
     */
    int getValue();

    /**
     * 获取枚举描述
     * @return 枚举说明
     */
    String getDesc();

}