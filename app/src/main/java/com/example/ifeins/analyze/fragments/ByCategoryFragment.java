package com.example.ifeins.analyze.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.activities.MainActivity;
import com.example.ifeins.analyze.adapters.TransactionsAdapter;
import com.example.ifeins.analyze.models.Transaction;
import com.example.ifeins.analyze.utils.AmountValueFormatter;
import com.example.ifeins.analyze.utils.MonthValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ByCategoryFragment extends Fragment implements TransactionsFragment, AdapterView.OnItemSelectedListener {

    private static final int BAR_ANIMATION_DURATION_MILLIS = 1000;

    private Spinner mCategoriesSpinner;
    private ArrayAdapter<String> mCategoriesAdapter;
    private ListView mListView;
    private TransactionsAdapter mAdapter;
    private BarChart mBarChartView;

    private Set<String> mCategories;
    private List<Transaction> mTransactions;

    public ByCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_by_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategoriesSpinner = (Spinner) view.findViewById(R.id.spinner_categories);
        mCategoriesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        mCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoriesSpinner.setAdapter(mCategoriesAdapter);
        mCategoriesSpinner.setOnItemSelectedListener(this);

        mBarChartView = (BarChart) view.findViewById(R.id.bar_chart);
        mBarChartView.setDescription(null);
        XAxis xAxis = mBarChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new MonthValueFormatter());
        YAxis axisLeft = mBarChartView.getAxisLeft();
        axisLeft.setValueFormatter(new AmountValueFormatter());
        axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        mBarChartView.getAxisRight().setDrawLabels(false);

        mListView = (ListView) view.findViewById(R.id.transactions_list_view);
        mAdapter = new TransactionsAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        if (mCategories != null && mTransactions != null) {
            updateUI();
        } else {
            MainActivity activity = (MainActivity) getActivity();
            setTransactions(activity.getTransactions());
        }
    }

    @Override
    public void setTransactions(List<Transaction> transactions) {
        mCategories = new HashSet<>();
        for (Transaction transaction : transactions) {
            mCategories.add(transaction.merchant.category.name);
        }

        String category = mCategories.iterator().next();
        Collection<Transaction> selectedCategoryTransactions = getTransactionsByCategory(transactions, category);
        mTransactions = new ArrayList<>(selectedCategoryTransactions);

        updateUI();
    }

    private void updateUI() {
        if (mCategories == null || mTransactions == null) {
            return;
        }

        if (mCategoriesSpinner != null) {
            mCategoriesAdapter.clear();
            mCategoriesAdapter.addAll(mCategories);
            mCategoriesAdapter.notifyDataSetChanged();
        }

        if (mAdapter != null) {
            mAdapter.setTransactions(mTransactions);
            mAdapter.notifyDataSetChanged();
        }

        if (mBarChartView != null) {
            addTransactionsToChart(mTransactions);
        }
    }

    private Collection<Transaction> getTransactionsByCategory(List<Transaction> transactions, final String category) {
        return Collections2.filter(transactions, new Predicate<Transaction>() {
                @Override
                public boolean apply(Transaction input) {
                    return input.merchant.category.name.equals(category);
                }
            });
    }

    private List<BarEntry> transactionsToBarEntries(final Collection<Transaction> transactions) {
        Function<Transaction, Integer> transactionDateFunction = new Function<Transaction, Integer>() {
            @Override
            public Integer apply(Transaction input) {
                return LocalDate.fromDateFields(input.date).getMonthOfYear();
            }
        };

        ImmutableListMultimap<Integer, Transaction> transactionsByMonth = Multimaps.index(transactions, transactionDateFunction);
        ImmutableMap<Integer, Collection<Transaction>> myMap = transactionsByMonth.asMap();
        Map<Integer, Float> transactionsValueByMonth = new HashMap<>();
        for (int monthOfYear : myMap.keySet()) {
            ImmutableList<Transaction> currentMonthTransactions = transactionsByMonth.get(monthOfYear);
            float sum = 0;
            for (Transaction transaction : currentMonthTransactions) {
                sum += transaction.amount / 100.0;
            }

            transactionsValueByMonth.put(monthOfYear, sum);
        }

        List<BarEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Float> entry : transactionsValueByMonth.entrySet()) {
            entries.add(new BarEntry(entry.getKey(), entry.getValue()));
        }

        return entries;
    }

    private void addTransactionsToChart(Collection<Transaction> transactions) {
        if (!transactions.isEmpty()) {
            List<BarEntry> entries = transactionsToBarEntries(transactions);
            BarDataSet dataSet = new BarDataSet(entries, "Money spent by month");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            BarData data = new BarData(dataSet);
            data.setBarWidth(0.9f);
            mBarChartView.setData(data);
            mBarChartView.invalidate();
            mBarChartView.animateXY(BAR_ANIMATION_DURATION_MILLIS, BAR_ANIMATION_DURATION_MILLIS);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String category = (String) mCategoriesSpinner.getAdapter().getItem(position);
        MainActivity activity = (MainActivity) getActivity();
        Collection<Transaction> transactionsByCategory = getTransactionsByCategory(activity.getTransactions(), category);
        mTransactions = new ArrayList<>(transactionsByCategory);

        updateUI();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
