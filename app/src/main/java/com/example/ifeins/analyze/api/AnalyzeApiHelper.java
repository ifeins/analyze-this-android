/*
 * Copyright (c) 2016 PayPal, Inc.
 *
 * All rights reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.example.ifeins.analyze.api;

import android.content.Context;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.models.User;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ifeins
 */
public class AnalyzeApiHelper {

    private static final String HEADER_USER_ID = "X-User-Id";

    public static AnalyzeApi createApi(Context context) {
        return retrofit(context).create(AnalyzeApi.class);
    }

    public static Retrofit retrofit(Context context) {
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                synchronized (User.class) {
                    if (User.getCurrentUser() != null) {
                        long id = User.getCurrentUser().getId();
                        Request request = chain.request().newBuilder().addHeader(HEADER_USER_ID, Long.toString(id)).build();
                        return chain.proceed(request);
                    } else {
                        // do nothing
                        return chain.proceed(chain.request());
                    }
                }
            }
        }).build();

        String baseUrl = context.getString(R.string.api_base_url);
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }
}
