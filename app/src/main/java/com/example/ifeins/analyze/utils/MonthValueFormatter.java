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

package com.example.ifeins.analyze.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import org.joda.time.LocalDate;

/**
 * TODO: Write Javadoc for MonthValueFormatter.
 *
 * @author ifeins
 */
public class MonthValueFormatter implements AxisValueFormatter {

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
