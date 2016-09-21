package com.example.ifeins.analyze.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ifeins.analyze.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ByCategoryFragment extends Fragment {


    public ByCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_by_category, container, false);
    }

}
