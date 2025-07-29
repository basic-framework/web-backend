package com.zl.model.enums.handler;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.function.Function;

/**
 * 枚举转换器
 * @param <T> 枚举类型
 * @Author GuihaoLv
 */
public  class EnumConverter<T extends Enum<T>> implements Converter<Integer>{
    private final Class<T> enumClass;
    private final Function<T, String> descGetter; // 枚举转描述的函数
    private final Function<String, T> codeParser; // 描述转枚举的函数（可选，用于导入）

    public EnumConverter(Class<T> enumClass, Function<T, String> descGetter) {
        this.enumClass = enumClass;
        this.descGetter = descGetter;
        // 反向解析（导入时用，可选实现）
        this.codeParser = s -> {
            for (T e : enumClass.getEnumConstants()) {
                if (descGetter.apply(e).equals(s)) {
                    return e;
                }
            }
            return null;
        };
    }

    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    // 导出时：整数 -> 字符串描述
    @Override
    public WriteCellData<String> convertToExcelData(Integer code, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (code == null) {
            return new WriteCellData<>("未知");
        }
        T enumValue = enumClass.getEnumConstants()[code]; // 假设枚举顺序与数据库一致
        return new WriteCellData<>(descGetter.apply(enumValue));
    }

    // 导入时：字符串描述 -> 整数（可选实现）
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String desc = cellData.getStringValue();
        if (desc == null) {
            return null;
        }
        T enumValue = codeParser.apply(desc);
        return enumValue != null ? ((Enum<?>) enumValue).ordinal() : null;
    }
}