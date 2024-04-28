package com.example.finalyear;

import com.github.mikephil.charting.data.BarEntry;

public class DateEntry {
    String date;
    BarEntry entry;

    DateEntry(String date, BarEntry entry) {
        this.date = date;
        this.entry = entry;
    }
}
