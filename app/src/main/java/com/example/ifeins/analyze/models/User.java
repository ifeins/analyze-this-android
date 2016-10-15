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

/**
 * @author ifeins
 */
public class User implements Parcelable {

    private static User sCurrentUser;

    private long id;

    private String email;

    private String name;

    public synchronized static void setCurrentUser(User user) {
        sCurrentUser = user;
    }

    public synchronized static User getCurrentUser() {
        return sCurrentUser;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    /** Parcelable implementation start **/

    protected User(Parcel in) {
        id = in.readLong();
        email = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(email);
        dest.writeString(name);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /** Parcelable implementation end **/
}
