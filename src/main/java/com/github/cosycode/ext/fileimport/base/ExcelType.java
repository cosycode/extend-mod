package com.github.cosycode.ext.fileimport.base;

import lombok.Getter;

/**
 * <b>Description : </b> excel 的类型, 当前类支持解析的 Excel 文件类型枚举类
 * <p>
 * <b>created in </b> 2020/7/22
 * </p>
 *
 * @author CPF
 **/
public enum ExcelType {
    /**
     * XLS, excel 早期格式
     */
    XLS(".xls"),
    /**
     * XLSX, excel 较新格式
     */
    XLSX(".xlsx");

    @Getter
    private final String fileSuffix;

    ExcelType(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    /**
     * 通过 文件名 获取对应枚举的类型.
     *
     * @param fileName 文件名
     * @return 对应的 枚举对象, 若找不到, 则返回 null
     */
    public static ExcelType getByJudgeSuffix(String fileName) {
        ExcelType[] values = ExcelType.values();
        for (ExcelType value : values) {
            if (fileName.endsWith(value.getFileSuffix())) {
                return value;
            }
        }
        return null;
    }

}
