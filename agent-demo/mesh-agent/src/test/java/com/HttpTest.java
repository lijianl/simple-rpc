package com;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * okHttp的两个基本使用 ->
 */
public class HttpTest {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public static String okGet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response res = null;
        try {
            res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                return res.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String okPost(String url, String data) {
        Map<String, String> headeMap = Maps.newHashMap();
        headeMap.put("name", "test");
        return okPost(url, data, headeMap);
    }


    /**
     * 通过回调 + future 实现 异步
     */
    public static String okPost(String url, String data, Map<String, String> headers) {


        Request request = new Request.Builder()
                .headers(Headers.of(headers))
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data))
                .url(url)
                .build();

        StrFuture strFuture = new StrFuture();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                strFuture.set(res);
            }
        });
        try {
            return strFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Headers makeHeader(Map<String, String> headerMap) {
        return Headers.of(headerMap);
    }


    public static void main(String[] args) {
        String url = "http://127.0.0.1:5000/hel";
        for (int i = 0; i < 100; i++) {
            JSONObject json = new JSONObject();
            json.put("key", "name" + i);
            String res = okPost(url, json.toString());
            System.out.println("res" + i + "=" + res);
        }
    }
}


/**
 * 通过future加回调完成异步使用
 */
class StrFuture implements Future<String> {

    private String data;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public String get() throws InterruptedException, ExecutionException {
        latch.await();  // wait set
        return data;
    }

    @Override
    public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);
        return data;
    }

    public void set(String str) {
        this.data = str;
        latch.countDown();
    }
}
