package com.example.ifeins.analyze.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.adapters.TabsAdapter;
import com.example.ifeins.analyze.api.AnalyzeApi;
import com.example.ifeins.analyze.api.AnalyzeApiHelper;
import com.example.ifeins.analyze.api.ApiError;
import com.example.ifeins.analyze.api.ErrorUtils;
import com.example.ifeins.analyze.models.Transaction;
import com.example.ifeins.analyze.models.TransactionsCache;
import com.example.ifeins.analyze.utils.DriveUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String STATE_TRANSACTIONS_FETCHED = "transactions_fetched";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int RC_OPEN_FILE = 1;
    public static final int RC_RESOLVE_CONNECTION = 2;
    public static final int RC_ERROR_DIALOG = 3;

    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;
    @BindView(R.id.pager)
    protected ViewPager mViewPager;

    private TabsAdapter mAdapter;

    private GoogleApiClient mGoogleApiClient;
    private FileDownloadCallback mFileDownloadCallback;
    private boolean mTransactionsFetched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAdapter = new TabsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        if (savedInstanceState != null) {
            mTransactionsFetched = savedInstanceState.getBoolean(STATE_TRANSACTIONS_FETCHED);
        }

        updateTransactions();
        if (!mTransactionsFetched) {
            fetchTransactions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_import_file:
                showImportFileDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showImportFileDialog() {
        IntentSender intentSender = new OpenFileActivityBuilder().build(mGoogleApiClient);
        try {
            startIntentSenderForResult(intentSender, RC_OPEN_FILE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(LOG_TAG, "Could not show open file dialog", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_OPEN_FILE:
                if (resultCode == RESULT_OK) {
                    DriveId fileId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    PendingResult<DriveApi.DriveContentsResult> result = fileId.asDriveFile().open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null);
                    mFileDownloadCallback = new FileDownloadCallback();
                    result.setResultCallback(mFileDownloadCallback);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fetchTransactions() {
        AnalyzeApi api = AnalyzeApiHelper.createApi(this);
        Call<List<Transaction>> transactionsCall = api.getTransactions();
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                TransactionsCache.getInstance().setTransactions(response.body());
                mTransactionsFetched = true;
                updateTransactions();
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                throw new RuntimeException(t);
            }

        });
    }

    private void updateTransactions() {
        List<Transaction> transactions = TransactionsCache.getInstance().getTransactions();
        if (transactions == null || transactions.isEmpty()) {
            mTabLayout.setVisibility(View.GONE);
        } else {
            mTabLayout.setVisibility(View.VISIBLE);
        }
        mAdapter.setTransactions(transactions);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_TRANSACTIONS_FETCHED, mTransactionsFetched);
    }

    /** Google API callbacks **/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_RESOLVE_CONNECTION);
            } catch (IntentSender.SendIntentException e) {
                Log.e(LOG_TAG, "Unable to resolve", e);
            }
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), RC_ERROR_DIALOG).show();
        }
    }

    /** End Google API callbacks **/

    private class FileDownloadCallback implements ResultCallback<DriveApi.DriveContentsResult> {

        @Override
        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
            final File tempFile;
            if (result.getStatus().isSuccess()) {
                tempFile = DriveUtils.createTempFileForDriveDocument(MainActivity.this.getCacheDir(), result.getDriveContents());
            } else {
                Toast.makeText(MainActivity.this, result.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.create(requestBody);
            String documentId = result.getDriveContents().getDriveId().getResourceId();
            Call<Void> importDocumentCall = AnalyzeApiHelper.createApi(MainActivity.this).createDocument(documentId, body);
            importDocumentCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    tempFile.delete();
                    if (response.isSuccessful()) {
                        fetchTransactions();
                    } else {
                        ApiError apiError = ErrorUtils.parseError(MainActivity.this, response);
                        Toast.makeText(MainActivity.this, "Failed to import document: " + apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    tempFile.delete();
                    Toast.makeText(MainActivity.this, "Failed to import document", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
