package com.github.cosycode.ext.web.http;

import com.github.cosycode.common.util.io.FileSystemUtils;
import com.github.cosycode.ext.se.util.JsonUtils;
import lombok.Data;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.*;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/5/14
 * </p>
 *
 * @author CPF
 **/
public class MyHttpDownloadResponseHandler implements HttpClientResponseHandler<MyHttpResponse> {

    private final String savePath;

    public MyHttpDownloadResponseHandler(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public MyHttpResponse handleResponse(ClassicHttpResponse response) throws IOException {
        int responseCode = response.getCode();
        HttpEntity entity = response.getEntity();
        DownloadResponse downloadResponse = new DownloadResponse();
        downloadResponse.setFilePath(savePath);
        downloadResponse.setFileLength(entity.getContentLength());
        downloadResponse.setStartTime(System.nanoTime());
        if (entity != null) {
            downloadResponse.setMessage("start downloading");
            File file = new File(savePath);
            FileSystemUtils.insureFileDirExist(file.getParentFile());
            try (InputStream inputStream = entity.getContent();
                 OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024 * 8];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    downloadResponse.addWriteLength(bytesRead);
                }
            }
            downloadResponse.setMessage("download success");
            downloadResponse.setEndTime(System.nanoTime());
        } else {
            downloadResponse.setMessage("Empty or non-existent file!");
        }
        return new MyHttpResponse(responseCode, JsonUtils.toJson(downloadResponse));
    }

    @Data
    public static class DownloadResponse {
        String contentType;
        long startTime;
        long endTime;
        String message;
        String filePath;
        long fileLength;
        long writeLength;

        private void addWriteLength(int length) {
            writeLength += length;
        }
    }
}
