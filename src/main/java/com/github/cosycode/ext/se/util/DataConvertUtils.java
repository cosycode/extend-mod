package com.github.cosycode.ext.se.util;

import com.github.cosycode.common.util.io.IoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * 提供文件数据的转换
 */
public class DataConvertUtils {

    private DataConvertUtils() {
    }

    /**
     * 将File转化成BASE64编码字符串
     *
     * @param file 文件对象
     * @return String base64编码
     *
     * @throws IOException 文件读取异常 & 文件未发现异常
     */

    public static String fileToBase64(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            final long length = file.length();
            if (length > Integer.MAX_VALUE) {
                throw new RuntimeException("the file is too large");
            }
            byte[] bytes = new byte[(int) length];
            final int read = in.read(bytes);
            Validate.isTrue(read == length, "文件读取长度和文件自身长度不匹配");
            return Base64.getEncoder().encodeToString(bytes);
        }
    }

    /**
     * 将BASE64字符串转换为File文件内容
     *
     * @param base64 base64位字符串
     * @param savePath 文件存储路径(包含文件名)
     * @throws IOException 写入文件异常
     */
    //
    public static void base64ToFile(String base64, String savePath) throws IOException {
        Validate.isTrue(StringUtils.isNotBlank(base64), "base64字符串不能为空");
        Validate.isTrue(StringUtils.isNotBlank(savePath), "存储路径savePath不能为空");
        IoUtils.insureFileExist(new File(savePath));
        byte[] bytes = Base64.getDecoder().decode(base64);
        IoUtils.writeFile(savePath, bytes);
    }


}
