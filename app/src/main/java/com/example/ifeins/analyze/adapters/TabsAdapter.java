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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;

import com.example.ifeins.analyze.fragments.AllTransactionsFragment;
import com.example.ifeins.analyze.fragments.ByCategoryFragment;
import com.example.ifeins.analyze.fragments.ByMerchantFragment;
import com.example.ifeins.analyze.fragments.NoTransactionsFragment;
import com.example.ifeins.analyze.fragments.OverviewFragment;
import com.example.ifeins.analyze.fragments.TransactionsFragment;
import com.example.ifeins.analyze.models.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write Javadoc for TabsAdapter.
 *
 * @author ifeins
 */
public class TabsAdapter extends FragmentPagerAdapter {

    private List<Pair<String, Fragment>> mFragments = new ArrayList<>();
    private Fragment mEmptyStateFragment = new NoTransactionsFragment();
    private boolean mTransactionsExist;

    public TabsAdapter(FragmentManager fm) {
        super(fm);
        mFragments.add(new Pair<String, Fragment>("Overview", new OverviewFragment()));
        mFragments.add(new Pair<String, Fragment>("All", new AllTransactionsFragment()));
        mFragments.add(new Pair<String, Fragment>("By category", new ByCategoryFragment()));
    }

    @Override
    public Fragment getItem(int position) {
        if (mTransactionsExist) {
            return mFragments.get(position).second;
        } else {
            return mEmptyStateFragment;
        }
    }

    @Override
    public int getCount() {
        if (mTransactionsExist) {
            return mFragments.size();
        } else {
            return 1;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTransactionsExist) {
            return mFragments.get(position).first;
        } else {
            return "No transactions";
        }
    }

    public void setTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            mTransactionsExist = false;
            return;
        }

        for (Pair<String, Fragment> pair : mFragments) {
            ((TransactionsFragment) pair.second).setTransactions(transactions);
        }
        mTransactionsExist = true;
    }
}
