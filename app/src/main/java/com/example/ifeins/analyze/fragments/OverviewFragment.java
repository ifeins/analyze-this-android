package com.example.ifeins.analyze.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.activities.MainActivity;
import com.example.ifeins.analyze.models.Category;
import com.example.ifeins.analyze.models.Transaction;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements TransactionsFragment {

    private static final int PIE_CHART_ANIMATION_DURATION_MILLIS = 1000;

    private static final String LOG_TAG = OverviewFragment.class.getSimpleName();

    private PieChart mPieChartView;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPieChartView = (PieChart) view.findViewById(R.id.pie_chart_view);
        mPieChartView.setDescription(null);
        mPieChartView.setDrawHoleEnabled(true);
        mPieChartView.setHoleColor(Color.WHITE);
        mPieChartView.setHoleRadius(58f);
        mPieChartView.setDrawCenterText(true);
        mPieChartView.setCenterText("Spending overview");
        mPieChartView.setCenterTextSize(20);

        MainActivity activity = (MainActivity) getActivity();
        List<Transaction> transactions = activity.getTransactions();
        if (transactions != null && !transactions.isEmpty()) {
            initPieChart(transactions);
        }
    }

    @Override
    public void setTransactions(List<Transaction> transactions) {
        if (mPieChartView == null) {
            return;
        }

        initPieChart(transactions);
    }

    private void initPieChart(List<Transaction> transactions) {
        List<PieEntry> entries = chartEntriesFromTransactions(transactions);
        PieDataSet dataSet = new PieDataSet(entries, "Money spent by category");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueFormatter(new MoneyFormatter());
        mPieChartView.setData(data);
        mPieChartView.animateY(PIE_CHART_ANIMATION_DURATION_MILLIS, Easing.EasingOption.EaseInOutQuad);
    }

    private List<PieEntry> chartEntriesFromTransactions(final List<Transaction> transactions) {
        Function<Transaction, String> transactionsCategoryFunction = new Function<Transaction, String>() {
            @Override
            public String apply(Transaction input) {
                return input.merchant.category.name;
            }
        };

        ImmutableMap<String, Collection<Transaction>> transactionsByCategory = Multimaps.index(transactions, transactionsCategoryFunction).asMap();
        Map<String, Integer> moneyPerCategory = new HashMap<>();
        for (Map.Entry<String, Collection<Transaction>> entry : transactionsByCategory.entrySet()) {
            float sum = 0;
            for (Transaction transaction : entry.getValue()) {
                sum += transaction.amount;
            }

            sum = sum / 100.f;
            moneyPerCategory.put(entry.getKey(), Math.round(sum));
        }

        List<PieEntry> pieEntries = new ArrayList<>();
        for (String category : moneyPerCategory.keySet()) {
            pieEntries.add(new PieEntry(moneyPerCategory.get(category), category));
        }

        return pieEntries;
    }

    private static class MoneyFormatter implements ValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.format("₪%d", Math.round(value));
        }
    }
}
