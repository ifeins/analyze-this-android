package com.example.ifeins.analyze.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.models.Transaction;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ByMerchantFragment extends Fragment implements TransactionsFragment {


    public ByMerchantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_by_merchant, container, false);
    }

    @Override
    public void setTransactions(List<Transaction> transactions) {

    }
}
