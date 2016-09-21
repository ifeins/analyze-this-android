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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * TODO: Write Javadoc for Transaction.
 *
 * @author ifeins
 */
public class Transaction implements Parcelable {

    protected Transaction(Parcel in) {
        amount = in.readInt();
        feeAmount = in.readInt();
        chargeAmount = in.readInt();
        formattedAmount = in.readString();
        formattedChargeAmount = in.readString();
        formattedFeeAmount = in.readString();
        paymentOrdinal = in.readInt();
        paymentCount = in.readInt();
        date = (Date) in.readSerializable();
        merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public int amount;

    @SerializedName("fee_amount")
    public int feeAmount;

    @SerializedName("charge_amount")
    public int chargeAmount;

    @SerializedName("amount_formatted")
    public String formattedAmount;

    @SerializedName("charge_amount_formatted")
    public String formattedChargeAmount;

    @SerializedName("fee_amount_formatted")
    public String formattedFeeAmount;

    @SerializedName("payment_ordinal")
    public int paymentOrdinal;

    @SerializedName("payment_count")
    public int paymentCount;

    @SerializedName("txn_date")
    public Date date;

    public Merchant merchant;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(amount);
        dest.writeInt(feeAmount);
        dest.writeInt(chargeAmount);
        dest.writeString(formattedAmount);
        dest.writeString(formattedChargeAmount);
        dest.writeString(formattedFeeAmount);
        dest.writeInt(paymentOrdinal);
        dest.writeInt(paymentCount);
        dest.writeSerializable(date);
        dest.writeParcelable(merchant, flags);
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
