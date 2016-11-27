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

import com.google.gson.annotations.SerializedName;

/**
 * Api errors model.
 *
 * @author ifeins
 */
public class ApiError {

    public enum ErrorCode {
        UNKNOWN,
        INSUFFICIENT_PERMISSIONS
    }

    @SerializedName("code")
    private ErrorCode mCode;

    @SerializedName("message")
    private String mMessage;
    
    public ApiError() {
    }

    public ApiError(ErrorCode code, String message) {
        mCode = code;
        mMessage = message;
    }

    public ErrorCode getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}
