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

/**
 * TODO: Write Javadoc for AmountValueFormatter.
 *
 * @author ifeins
 */
public class AmountValueFormatter implements AxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.format("₪%d", Math.round(value));
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
