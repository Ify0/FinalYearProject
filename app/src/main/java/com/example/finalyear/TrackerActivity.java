package com.example.finalyear;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrackerActivity extends AppCompatActivity {

    private BarChart barChart;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private List<String> xValues;
    private HashMap<String, Integer> skinConditionColors;
    private HashMap<String, String> dateToPredictedClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker);

        barChart = findViewById(R.id.barChart);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        skinConditionColors = new HashMap<>();
        skinConditionColors.put("hyperpigmentation", Color.RED);
        skinConditionColors.put("pimples", Color.GREEN);
        skinConditionColors.put("fine_lines", Color.BLUE);
        skinConditionColors.put("wrinkles", Color.YELLOW);
        skinConditionColors.put("enlarged_pores", Color.MAGENTA);
        skinConditionColors.put("dark_circles", Color.CYAN);

        retrieveAnalysisResult();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click event
                onBackPressed();
            }
        });

        ImageButton infoButtonTop = findViewById(R.id.infoButtonTop);
        infoButtonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TrackerActivity.this)
                        .setTitle("Color Palette Information")
                        .setMessage("The colors represent different skin conditions:\n" +
                                "Red - Hyperpigmentation\n" +
                                "Green - Pimples\n" +
                                "Blue - Fine Lines\n" +
                                "Yellow - Wrinkles\n" +
                                "Magenta - Enlarged Pores\n" +
                                "Cyan - Dark Circles")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss the dialog when the positive button is clicked
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }

    private void retrieveAnalysisResult() {
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("uploads")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<BarEntry> entries = new ArrayList<>();
                        xValues = new ArrayList<>();
                        dateToPredictedClass = new HashMap<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String analysisResult = document.getString("analysisResult");
                            float confidence = extractConfidenceClass(analysisResult);
                            String predictedClass = extractPredictedClass(analysisResult);

                            Date date = document.getDate("timestamp");
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            String formattedDate = dateFormat.format(date);
                            xValues.add(formattedDate);

                            dateToPredictedClass.put(formattedDate, predictedClass);

                            int xIndex = xValues.indexOf(formattedDate);
                            entries.add(new BarEntry(xIndex, confidence));
                        }

                        setupBarChart(entries);
                    } else {
                        // Handle error
                    }
                });
    }

    private void setupBarChart(List<BarEntry> entries) {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Adjust granularity as needed
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(11);

        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);

        BarDataSet dataSet = new BarDataSet(entries, "Skin Conditions");
        dataSet.setColors(getColorPalette());
        dataSet.setValueTextColor(Color.TRANSPARENT); // Hide values on bars
        dataSet.setValueTextSize(0f); // Hide values on bars

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // Increase bar width for aesthetics

        barChart.setData(barData);
        barChart.invalidate();
    }

    private float extractConfidenceClass(String analysisResult) {
        String[] parts = analysisResult.split("Confidence: ");
        if (parts.length > 1) {
            String confidenceStr = parts[1].split(",")[0].trim();
            float confidence = Float.parseFloat(confidenceStr);
            // Convert confidence to percentage
            return confidence * 100f;
        }
        return 0;
    }

    private String extractPredictedClass(String analysisResult) {
        String[] parts = analysisResult.split("Predicted Class: ");
        if (parts.length > 1) {
            String classAndConfidence = parts[1].trim();
            return classAndConfidence.split(",")[0].trim();
        }
        return "";
    }

    private List<Integer> getColorPalette() {
        // Return colors in the order of xValues (dates)
        List<Integer> colors = new ArrayList<>();
        for (String date : xValues) {
            String predictedClass = dateToPredictedClass.get(date);
            colors.add(skinConditionColors.get(predictedClass));
        }
        return colors;
    }
}
