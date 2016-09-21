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
public class OverviewFragment extends Fragment implements TransactionsFragment {

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void setTransactions(List<Transaction> transactions) {

    }
}
