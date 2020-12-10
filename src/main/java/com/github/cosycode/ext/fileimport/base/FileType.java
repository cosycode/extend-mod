package com.github.cosycode.ext.fileimport.base;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/7/22 17:42
 */
public enum FileType {
    XLS,
    XLSX;

    public static FileType getByJudgeSuffix(String fileName) {
        if (fileName.endsWith(".xls")) {
            return XLS;
        } else if (fileName.endsWith(".xlsx")) {
            return XLSX;
        } else {
            return null;
        }
    }

}
