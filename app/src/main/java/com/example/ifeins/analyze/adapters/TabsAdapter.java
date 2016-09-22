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
import com.example.ifeins.analyze.fragments.OverviewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write Javadoc for TabsAdapter.
 *
 * @author ifeins
 */
public class TabsAdapter extends FragmentPagerAdapter {

    private List<Pair<String, Fragment>> mFragments = new ArrayList<>();

    public TabsAdapter(FragmentManager fm) {
        super(fm);
        mFragments.add(new Pair<String, Fragment>("Overview", new OverviewFragment()));
        mFragments.add(new Pair<String, Fragment>("All", new AllTransactionsFragment()));
        mFragments.add(new Pair<String, Fragment>("By category", new ByCategoryFragment()));
//        mFragments.add(new Pair<String, Fragment>("By merchant", new ByMerchantFragment()));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position).second;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).first;
    }
}
