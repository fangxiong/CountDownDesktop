package com.fax.showdt.utils;

import com.ali.ha.fulltrace.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpClientHelper {

    public static OkHttpClient getOkHttpClient() {

        try {
            SSLContext.getInstance("SSL");
        } catch (Throwable ignored) {
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            private StringBuilder messages = new StringBuilder();
            private final int JSON_INDENT = 2;

            @Override
            public void log(String message) {
                try {
                    if (message.startsWith("{") && message.endsWith("}")) {
                        JSONObject jsonObject = new JSONObject(message);
                        message = jsonObject.toString(JSON_INDENT);
                    } else if (message.startsWith("[") && message.endsWith("]")) {
                        JSONArray jsonArray = new JSONArray(message);
                        message = jsonArray.toString(JSON_INDENT);
                    }
                    messages.append(message);
                    messages.append("\n");
                    if (message.startsWith("<-- END HTTP")) {
                        Logger.i("test_http:",messages.toString());
                        messages.delete(0, messages.length());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .protocols(Util.immutableList(Protocol.HTTP_1_1))
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(interceptor);
        return builder.build();
    }
}
