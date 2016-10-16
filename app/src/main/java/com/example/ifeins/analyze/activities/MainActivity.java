package com.example.ifeins.analyze.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.adapters.TabsAdapter;
import com.example.ifeins.analyze.api.AnalyzeApi;
import com.example.ifeins.analyze.api.AnalyzeApiHelper;
import com.example.ifeins.analyze.models.Transaction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String STATE_TRANSACTIONS = "transactions";
    private static final String STATE_TRANSACTIONS_FETCHED = "transactions_fetched";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int RC_OPEN_FILE = 1;
    public static final int RC_RESOLVE_CONNECTION = 2;
    public static final int RC_ERROR_DIALOG = 3;

    private TabsAdapter mAdapter;
    private ViewPager mViewPager;

    private List<Transaction> mTransactions = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private DisplayFileTitleCallback mDisplayFileTitleCallback;
    private boolean mTransactionsFetched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new TabsAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        if (savedInstanceState != null) {
            mTransactions = savedInstanceState.getParcelableArrayList(STATE_TRANSACTIONS);
            mTransactionsFetched = savedInstanceState.getBoolean(STATE_TRANSACTIONS_FETCHED);
        }

        if (mTransactionsFetched) {
            mAdapter.setTransactions(mTransactions);
        } else {
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
                    PendingResult<DriveResource.MetadataResult> result = fileId.asDriveFile().getMetadata(mGoogleApiClient);
                    mDisplayFileTitleCallback = new DisplayFileTitleCallback();
                    result.setResultCallback(mDisplayFileTitleCallback);
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
                mTransactions = response.body();
                mTransactionsFetched = true;
                mAdapter.setTransactions(mTransactions);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                throw new RuntimeException(t);
            }

        });
    }

    public List<Transaction> getTransactions() {
        return mTransactions;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_TRANSACTIONS, (ArrayList<? extends Parcelable>) mTransactions);
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

    private class DisplayFileTitleCallback implements ResultCallback<DriveResource.MetadataResult> {

        @Override
        public void onResult(@NonNull DriveResource.MetadataResult metadataResult) {
            if (metadataResult.getStatus().isSuccess()) {
                Toast.makeText(MainActivity.this,
                        "File opened: " + metadataResult.getMetadata().getTitle(),
                        Toast.LENGTH_SHORT).show();
            } else {
                String errorMessage = metadataResult.getStatus().getStatusMessage();
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
