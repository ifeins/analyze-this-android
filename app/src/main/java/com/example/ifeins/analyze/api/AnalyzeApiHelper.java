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

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ifeins
 */
public class AnalyzeApiHelper {

    public static AnalyzeApi createApi(Context context) {
        String baseUrl = context.getString(R.string.api_base_url);
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create()).
                build();
        return retrofit.create(AnalyzeApi.class);
    }
}
