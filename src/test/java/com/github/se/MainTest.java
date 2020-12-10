package com.github.se;

import com.github.cosycode.common.util.io.FileSystemUtils;

import java.io.*;
import java.nio.charset.Charset;

public class MainTest {

    public void main() throws FileNotFoundException {
        FileSystemUtils.fileDisposeFromDir(new File("C:\\Users\\Private\\read-now\\red"), file -> {
            try {
                System.out.println(new FileReader(file).getEncoding());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }, null);

    }


    /**
     * 输入流转String
     *
     * @param inputStream 输入流
     * @param charset 编码
     * @return 转换的字符串
     */
    public static String readStringFromInputStream(InputStream inputStream, Charset charset) throws IOException {
        try (InputStreamReader inputStreamReader = charset == null ?
                new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charset)) {
            //字符缓冲流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //字符串缓冲区
            StringBuilder stringBuilder = new StringBuilder();
            String len;
            //按行读
            while((len=bufferedReader.readLine()) != null){
                //追加到字符串缓冲区存放
                stringBuilder.append(len);
            }
            //将字符串返回
            return stringBuilder.toString();
        }
    }

    /**
     * 判断文件的编码格式
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(File fileName){
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName))){
            String charset = "GBK";
            byte[] first3Bytes = new byte[3];
            boolean checked = false;
            bis.mark(0); // 读者注： bis.mark(0);修改为 bis.mark(100);我用过这段代码，需要修改上面标出的地方。
            // Wagsn注：不过暂时使用正常，遂不改之
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                bis.close();
                return charset; // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            return charset;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "null";
    }

}
