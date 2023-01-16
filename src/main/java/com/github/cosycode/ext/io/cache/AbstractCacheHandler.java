package com.github.cosycode.ext.io.cache;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.util.io.FileSystemUtils;
import com.github.cosycode.common.util.io.IoUtils;
import com.github.cosycode.ext.se.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.nio.charset.StandardCharsets;

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
public abstract class AbstractCacheHandler<T extends ICacheStack> {

    @Getter
    private String tag;

    public abstract void put(T t);

    public abstract T get();

    public boolean validate(T t) {
        return t.validate();
    }

    public void clear() {
        put(null);
    }

    public static <T extends ICacheStack> AbstractCacheHandler<T> geneMemoryCacheHandler(String tag) {
        return new AbstractCacheHandler<T>(tag) {

            T value;

            @Override
            public void put(T value) {
                this.value = value;
            }

            @Override
            public T get() {
                return value;
            }
        };
    }

    public static <T extends ICacheStack> AbstractCacheHandler<T> geneFileCacheHandler(String tag, @NonNull String filePath, Class<T> tClass) {
        // 此处 filePath 为闭包
        return new AbstractCacheHandler<T>(tag + " => " + filePath) {

            @Override
            public void put(T value) {
                File file = new File(filePath);
                FileSystemUtils.insureFileExist(file);
                Throws.runtimeEpt(() -> {
                    IoUtils.writeFile(file.getPath(), JsonUtils.toJson(value).getBytes(StandardCharsets.UTF_8));
                });
            }

            @Override
            public T get() {
                File file = new File(filePath);
                if (file.exists()) {
                    String response = Throws.runtimeEpt(() -> IoUtils.readFile(file));
                    return JsonUtils.fromJson(response, tClass);
                }
                return null;
            }

            @Override
            public void clear() {
                File file = new File(filePath);
                file.deleteOnExit();
            }
        };
    }
}
