package com.example.ifeins.analyze.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ifeins.analyze.R;
import com.example.ifeins.analyze.models.Transaction;
import com.example.ifeins.analyze.models.TransactionsCache;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements TransactionsFragment {

    private static final int PIE_CHART_ANIMATION_DURATION_MILLIS = 1000;
    // only showing categories that are more than 3%
    private static final float PIE_ENTRY_THRESHOLD = 0.03f;

    private static final String LOG_TAG = OverviewFragment.class.getSimpleName();

    @BindView(R.id.pie_chart_view)
    protected PieChart mPieChartView;
    @BindView(R.id.empty_state_view)
    protected TextView mEmptyStateView;

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
        ButterKnife.bind(this, view);

        mPieChartView.setDescription(null);
        mPieChartView.setUsePercentValues(true);
        mPieChartView.setDrawHoleEnabled(true);
        mPieChartView.setHoleColor(Color.WHITE);
        mPieChartView.setHoleRadius(58f);
        mPieChartView.setDrawCenterText(true);
        mPieChartView.setCenterText("Spending overview");
        mPieChartView.setCenterTextSize(20);
        mPieChartView.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        mPieChartView.setDrawEntryLabels(false);

        Legend legend = mPieChartView.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);

        setTransactions(TransactionsCache.getInstance().getTransactions());
    }

    @Override
    public void setTransactions(List<Transaction> transactions) {
        if (mPieChartView == null) {
            return;
        }

        if (transactions != null && !transactions.isEmpty()) {
            mEmptyStateView.setVisibility(View.GONE);
            mPieChartView.setVisibility(View.VISIBLE);
            initPieChart(transactions);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mPieChartView.setVisibility(View.GONE);
        }
    }

    private void initPieChart(List<Transaction> transactions) {
        List<PieEntry> entries = chartEntriesFromTransactions(transactions);
        PieDataSet dataSet = new PieDataSet(entries, "Money spent by category");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueFormatter(new PercentFormatter());
        mPieChartView.setData(data);
        mPieChartView.animateY(PIE_CHART_ANIMATION_DURATION_MILLIS, Easing.EasingOption.EaseInOutQuad);
    }

    private List<PieEntry> chartEntriesFromTransactions(final List<Transaction> transactions) {
        int totalTransactionsSum = 0;
        for (Transaction transaction : transactions) {
            totalTransactionsSum += transaction.amount;
        }
        totalTransactionsSum = Math.round(totalTransactionsSum / 100.f);

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
        int otherCategorySum = 0;
        for (String category : moneyPerCategory.keySet()) {
            int categoryValue = moneyPerCategory.get(category);
            if (categoryValue > totalTransactionsSum * PIE_ENTRY_THRESHOLD) {
                pieEntries.add(new PieEntry(categoryValue, category));
            } else {
                otherCategorySum += categoryValue;
            }
        }
        if (otherCategorySum > 0) {
            pieEntries.add(new PieEntry(otherCategorySum, "Other"));
        }

        return pieEntries;
    }

    private static class PercentFormatter implements ValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return Math.round(value) + "%";
        }
    }
}
