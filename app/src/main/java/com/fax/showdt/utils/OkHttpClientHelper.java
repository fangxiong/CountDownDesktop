package com.fax.showdt.utils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.Util;

public class OkHttpClientHelper {

    public static OkHttpClient getOkHttpClient() {

        try {
            SSLContext.getInstance("SSL");
        } catch (Throwable ignored) {
        }
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .protocols(Util.immutableList(Protocol.HTTP_1_1))
                .followRedirects(true)
                .followSslRedirects(true);
        return builder.build();
    }
}
