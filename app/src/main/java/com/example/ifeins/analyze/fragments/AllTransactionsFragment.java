package com.example.ifeins.analyze.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.activities.MainActivity;
import com.example.ifeins.analyze.adapters.TransactionsAdapter;
import com.example.ifeins.analyze.models.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllTransactionsFragment extends Fragment implements TransactionsFragment {

    private static final int BAR_ANIMATION_DURATION_MILLIS = 1000;

    private ListView mListView;
    private TransactionsAdapter mAdapter;
    private BarChart mBarChartView;

    public AllTransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_transactions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.transactions_list_view);

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

        MainActivity activity = (MainActivity) getActivity();
        List<Transaction> transactions = activity.getTransactions();
        mAdapter = new TransactionsAdapter(getActivity(), transactions);
        mListView.setAdapter(mAdapter);

        addTransactionsToChart(transactions);
    }

    @Override
    public void setTransactions(List<Transaction> transactions) {
        if (mAdapter != null) {
            mAdapter.setTransactions(transactions);
            addTransactionsToChart(transactions);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mBarChartView.getData().getDataSetCount() > 0) {
                mBarChartView.animateXY(BAR_ANIMATION_DURATION_MILLIS, BAR_ANIMATION_DURATION_MILLIS);
            }
        }
    }

    private List<BarEntry> transactionsToBarEntries(final List<Transaction> transactions) {
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

    private void addTransactionsToChart(List<Transaction> transactions) {
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

    private static class MonthValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            LocalDate date = new LocalDate(2000, (int) value, 1);
            return date.toString("MMM");
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    private static class AmountValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.format("₪%d", Math.round(value));
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

}
