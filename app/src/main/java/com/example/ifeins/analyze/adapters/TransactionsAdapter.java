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

package com.example.ifeins.analyze.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.models.Transaction;
import com.example.ifeins.analyze.views.TransactionRowView;

import java.util.List;

/**
 * TODO: Write Javadoc for TransactionsAdapter.
 *
 * @author ifeins
 */
public class TransactionsAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Transaction> mTransactions;

    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        mContext = context;
        mTransactions = transactions;
    }

    @Override
    public int getCount() {
        return mTransactions.size();
    }

    @Override
    public Transaction getItem(int position) {
        return mTransactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TransactionRowView transactionRowView;
        if (convertView != null) {
            transactionRowView = (TransactionRowView) convertView;
        } else {
            transactionRowView = new TransactionRowView(mContext);
        }

        transactionRowView.setTransaction(getItem(position));
        return transactionRowView;
    }

    public void setTransactions(List<Transaction> transactions) {
        mTransactions = transactions;
        notifyDataSetChanged();
    }
}
