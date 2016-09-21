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

package com.example.ifeins.analyze.views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.models.Transaction;
import com.squareup.picasso.Picasso;

/**
 * TODO: Write Javadoc for TransactionRowView.
 *
 * @author ifeins
 */
public class TransactionRowView extends RelativeLayout {

    private final TextView mTitleView;
    private final ImageView mImageView;
    private final TextView mAmountView;

    public TransactionRowView(Context context) {
        super(context);
    }

    {
        inflate(getContext(), R.layout.view_transaction_row, this);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        int padding = (int) (getResources().getDisplayMetrics().density * 16);
        setPadding(padding, padding, padding, padding);
        mTitleView = (TextView) findViewById(R.id.transaction_row_merchant_view);
        mImageView = (ImageView) findViewById(R.id.transaction_row_image_view);
        mAmountView = (TextView) findViewById(R.id.transaction_row_amount_view);
    }

    public void setTransaction(Transaction transaction) {
        mTitleView.setText(transaction.merchant.name);
        mAmountView.setText(transaction.formattedAmount);
        if (transaction.merchant.logoUrl != null) {
            Picasso.with(getContext()).load(transaction.merchant.logoUrl).into(mImageView);
        } else {
            mImageView.setImageResource(R.drawable.ic_vector_merchant);
        }
    }
}
