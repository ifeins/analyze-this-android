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

import com.example.ifeins.analyze.fragments.AllTransactionsFragment;
import com.example.ifeins.analyze.fragments.ByCategoryFragment;
import com.example.ifeins.analyze.fragments.OverviewFragment;

/**
 * TODO: Write Javadoc for MyAdapter.
 *
 * @author ifeins
 */
public class MyAdapter extends FragmentPagerAdapter {

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OverviewFragment();
            case 1:
                return new AllTransactionsFragment();
            case 2:
                return new ByCategoryFragment();
            default:
                throw new IllegalArgumentException("Unsupported tab");
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Overview";
            case 1:
                return "All transactions";
            case 2:
                return "Transactions by category";
            default:
                throw new IllegalArgumentException("Unsupported tab");
        }
    }
}
