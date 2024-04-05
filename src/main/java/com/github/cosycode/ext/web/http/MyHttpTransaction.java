package com.github.cosycode.ext.web.http;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.util.function.BiFunction;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/8/20
 * </p>
 **/
@Getter
@Accessors(fluent = true, chain = true)
public class MyHttpTransaction {

    @SuppressWarnings("java:S2065")
    private final transient MyHttpClient client;
    private final MyHttpRequest request;
    protected MyHttpResponse response;

    public static MyHttpTransaction generate(MyHttpClient client, MyHttpRequest request) {
        return new MyHttpTransaction(client, request);
    }

    public MyHttpTransaction(MyHttpClient client, MyHttpRequest request) {
        this.client = client;
        this.request = request;
    }

    public MyHttpResponse send() throws IOException {
        return client.send(request);
    }

    public <R> R send(@NonNull MyHttpTransactionFunction<R> responseDisposer) throws IOException {
        return new Handler<R>(this.client, this.request, responseDisposer).sendRequest();
    }

    public <R> R sendAndCatchException(@NonNull MyHttpTransactionFunction<R> responseDisposer, BiFunction<Handler<R>, Exception, R> catchDisposer) {
        Handler<R> httpTransaction = new Handler<>(this.client, this.request, responseDisposer);
        try {
            return httpTransaction.sendRequest();
        } catch (IOException e) {
            if (catchDisposer == null) {
                throw new RuntimeException(e);
            }
            return catchDisposer.apply(httpTransaction, e);
        }
    }

    public <R> R sendWithRuntimeException(@NonNull MyHttpTransactionFunction<R> responseDisposer) {
        return sendAndCatchException(responseDisposer, null);
    }

    @FunctionalInterface
    public interface MyHttpTransactionFunction<R> {
        R apply(Handler<R> httpTransaction, MyHttpResponse response) throws IOException;
    }

    @SuppressWarnings("java:S2065")
    public class Handler<R> extends MyHttpTransaction {
        private final transient MyHttpTransactionFunction<R> responseDisposer;
        @Setter(value = AccessLevel.NONE)
        private int retryTime;

        public Handler(MyHttpClient client, MyHttpRequest request, @NonNull MyHttpTransactionFunction<R> responseDisposer) {
            super(client, request);
            this.responseDisposer = responseDisposer;
        }

        public MyHttpResponse retry(int cnt) throws IOException {
            if (cnt > retryTime) {
                sendRequest();
            }
            throw new IOException(String.format("try %s times, and all fails ==> %s", retryTime, this));
        }

        private R sendRequest() throws IOException {
            retryTime++;
            this.response = client.send(request);
            return responseDisposer.apply(this, this.response);
        }

        @Override
        public String toString() {
            return "MyHttpTransaction{" + "request=" + request + ", response=" + response + ", retryTime=" + retryTime + '}';
        }

    }

    @Override
    public String toString() {
        return "MyHttpTransaction{" + "request=" + request + ", response=" + response + '}';
    }

}