package com.example.ifeins.analyze.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.adapters.TabsAdapter;
import com.example.ifeins.analyze.api.AnalyzeApi;
import com.example.ifeins.analyze.fragments.TransactionsFragment;
import com.example.ifeins.analyze.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_TRANSACTIONS = "transactions";

    private TabsAdapter mAdapter;
    private ViewPager mViewPager;

    private List<Transaction> mTransactions = new ArrayList<>();

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
        }

        if (mTransactions != null && !mTransactions.isEmpty()) {
            updateFragments();
        } else {
            fetchTransactions();
        }
    }

    private void fetchTransactions() {
        String baseUrl = getString(R.string.api_base_url);
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create()).
                build();
        AnalyzeApi analyzeApi = retrofit.create(AnalyzeApi.class);

        Call<List<Transaction>> transactionsCall = analyzeApi.getTransactions();
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                mTransactions = response.body();
                updateFragments();
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                throw new RuntimeException(t);
            }

        });
    }

    private void updateFragments() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TransactionsFragment fragment = (TransactionsFragment) mAdapter.getItem(i);
            fragment.setTransactions(mTransactions);
        }
    }

    public List<Transaction> getTransactions() {
        return mTransactions;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_TRANSACTIONS, (ArrayList<? extends Parcelable>) mTransactions);
    }
}
