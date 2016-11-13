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

package com.example.ifeins.analyze.models;

import java.util.List;

/**
 * An in-memory cache that stores the fetched transactions.
 *
 * @author ifeins
 */
public class TransactionsCache {

    private static TransactionsCache sInstance = new TransactionsCache();

    private List<Transaction> mTransactions;

    public static TransactionsCache getInstance() {
        return sInstance;
    }

    public synchronized List<Transaction> getTransactions() {
        return mTransactions;
    }

    public synchronized void setTransactions(List<Transaction> transactions) {
        mTransactions = transactions;
    }
}
