package com.github.cosycode.ext.io.cache;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.lang.BaseRuntimeException;
import com.github.cosycode.common.util.io.FileSystemUtils;
import com.github.cosycode.common.util.io.IoUtils;
import com.github.cosycode.common.util.io.PropsUtil;
import com.github.cosycode.ext.se.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/8
 * </p>
 *
 * @author pengfchen
 * @since 0.2.2
 **/
@AllArgsConstructor
public abstract class AbstractMapCacheHandler<T> {

    @Getter
    private String tag;

    public abstract void put(String key, T t);

    public abstract T get(String key);

    public boolean validate(String key, T t) {
        return true;
    }

    public void clear(String key) {
        put(key, null);
    }

    public static <T> AbstractMapCacheHandler<T> geneMemoryCacheHandler(String tag) {
        return new AbstractMapCacheHandler<T>(tag) {

            T value;

            @Override
            public void put(String key, T value) {
                this.value = value;
            }

            @Override
            public T get(String key) {
                return value;
            }
        };
    }

    public static <T> AbstractMapCacheHandler<T> genePropertiesCacheHandler(String tag, @NonNull String filePath, Class<T> tClass) {
        // 此处 filePath 为闭包
        return new AbstractMapCacheHandler<T>(tag + " => " + filePath) {

            private File file;
            private Properties properties;

            {
                try {
                    file = new File(filePath);
                    if (file.exists()) {
                        properties = PropsUtil.loadProps(filePath);
                    } else {
                        properties = new Properties();
                    }
                } catch (IOException e) {
                    throw new BaseRuntimeException("", e);
                }
            }

            @Override
            public void put(String key, T value) {
                FileSystemUtils.insureFileExist(file);
                properties.put(key, JsonUtils.toJson(value));
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    properties.store(outputStream, "put key: " + key);
                } catch (IOException e) {
                    throw new BaseRuntimeException("", e);
                }
            }

            @Override
            public T get(String key) {
                Object o = properties.get(key);
                if (o == null) {
                    return null;
                }
                String response = o.toString();
                return JsonUtils.fromJson(response, tClass);
            }

            @Override
            public void clear(String key) {
                properties = new Properties();
                file.deleteOnExit();
            }
        };
    }

    public static <T> AbstractMapCacheHandler<T> geneFileCacheHandler(String tag, @NonNull String fileDir, Class<T> tClass) {
        // 此处 filePath 为闭包
        return new AbstractMapCacheHandler<T>(tag + " => " + fileDir) {

            private String getFilePath(String key) {
                return fileDir + key;
            }

            @Override
            public void put(String key, T value) {
                File file = new File(getFilePath(key));
                FileSystemUtils.insureFileExist(file);
                Throws.runtimeEpt(() -> {
                    IoUtils.writeFile(file.getPath(), JsonUtils.toJson(value).getBytes(StandardCharsets.UTF_8));
                });
            }

            @Override
            public T get(String key) {
                File file = new File(getFilePath(key));
                if (file.exists()) {
                    String response = Throws.runtimeEpt(() -> IoUtils.readFile(file));
                    return JsonUtils.fromJson(response, tClass);
                }
                return null;
            }

            @Override
            public void clear(String key) {
                File file = new File(getFilePath(key));
                file.deleteOnExit();
            }
        };
    }
}
