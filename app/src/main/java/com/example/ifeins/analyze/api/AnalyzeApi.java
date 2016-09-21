package com.example.ifeins.analyze.api;

import com.example.ifeins.analyze.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ifeins on 9/21/16.
 */
public interface AnalyzeApi {

    @GET("transactions")
    Call<List<Transaction>> getTransactions();

}
