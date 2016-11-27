package com.example.ifeins.analyze.api;

import com.example.ifeins.analyze.models.Transaction;
import com.example.ifeins.analyze.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by ifeins on 9/21/16.
 */
public interface AnalyzeApi {

    @POST("auth/google")
    Call<User> signInWithGoogle(@Query("id_token") String idToken);

    @GET("transactions")
    Call<List<Transaction>> getTransactions();

    @Multipart
    @POST("documents")
    Call<Void> createDocument(@Query("document_id") String documentId, @Part MultipartBody.Part file);

}
