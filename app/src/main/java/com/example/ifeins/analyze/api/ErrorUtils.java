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

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Utility class for parsing errors.
 *
 * @author ifeins
 */
public class ErrorUtils {

    public static ApiError parseError(Context context, Response<?> response) {
        Retrofit retrofit = AnalyzeApiHelper.retrofit(context);
        Converter<ResponseBody, ApiError> responseBodyConverter = retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError error;
        try {
            error = responseBodyConverter.convert(response.errorBody());
        } catch (IOException e) {
            error = new ApiError(ApiError.ErrorCode.UNKNOWN, null);
        }

        return error;
    }
}
