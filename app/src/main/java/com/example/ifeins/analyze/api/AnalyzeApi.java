package com.example.ifeins.analyze.api;

import com.example.ifeins.analyze.models.Transaction;
import com.example.ifeins.analyze.models.User;
import com.google.android.gms.drive.DriveId;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ifeins on 9/21/16.
 */
public interface AnalyzeApi {

    @POST("user")
    Call<User> signIn(@Query("id_token") String idToken, @Query("auth_code") String authCode, @Query("refresh_tokens") boolean refreshTokens);

    @GET("transactions")
    Call<List<Transaction>> getTransactions();

    @POST("documents/import_google_drive_resource")
    Call<Void> importGoogleDriveResource(@Query("drive_id") String driveId);

}
