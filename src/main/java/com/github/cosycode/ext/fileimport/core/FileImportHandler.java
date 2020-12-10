package com.github.cosycode.ext.fileimport.core;

import lombok.NonNull;

import java.io.InputStream;

/**
 * <b>Description : </b> 读取并解压分发文件
 * 1. 设置配置
 * 2. 读取文件
 * 3. 将数据解析为记录
 * 4. 验证
 * 5. 后处理
 * 6. 导入数据库或更新数据库
 *
 *
 * @author CPF
 * Date: 2020/7/22 16:15
 */
public class FileImportHandler {

    /**
     * 从文件中读取数据
     */
    public static void readFromSource(@NonNull InputStream is, String fileName, AbstractFileImportAdapter adapter) {
        getResolver().resolve(is, fileName, adapter).persistence();
    }

    private static AbstractFileResolver getResolver() {
        return null;
    }

}
