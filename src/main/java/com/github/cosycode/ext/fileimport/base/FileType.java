package com.github.cosycode.ext.fileimport.base;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2020/7/22
 * </p>
 *
 * @author CPF
 **/
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
