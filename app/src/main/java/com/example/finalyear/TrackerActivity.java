package com.example.finalyear;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackerActivity extends AppCompatActivity {

    private LineChart lineChart;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private List<String> xValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker);

        lineChart = findViewById(R.id.lineChart);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        retrieveAnalysisResult();
    }

    private void retrieveAnalysisResult() {
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("uploads")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Entry> darkCirclesEntries = new ArrayList<>();
                        List<Entry> enlargedPoresEntries = new ArrayList<>();
                        List<Entry> pimplesEntries = new ArrayList<>();
                        List<Entry> hyperpigmentationEntries = new ArrayList<>();
                        List<Entry> wrinklesEntries = new ArrayList<>();
                        List<Entry> fineLinesEntries = new ArrayList<>();

                        xValues = Arrays.asList("Dark Circles", "Hyperpigmentation", "Enlarged Pores", "Fine Lines", "Wrinkles");

                        Description description = new Description();
                        description.setText("Skin Progress");
                        description.setPosition(150f, 15f);
                        lineChart.setDescription(description);
                        lineChart.getAxisRight().setDrawLabels(false);

                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
                        xAxis.setLabelCount(6);
                        xAxis.setGranularity(1f); // Adjust granularity as needed

                        YAxis yAxis = lineChart.getAxisLeft();
                        yAxis.setAxisMinimum(0f); // Set minimum value for Y-axis
                        yAxis.setAxisMaximum(100f); // Set maximum value for Y-axis
                        yAxis.setAxisLineWidth(2f);
                        yAxis.setAxisLineColor(Color.BLACK);
                        yAxis.setLabelCount(11); // Adjust label count as needed

                        for (DocumentSnapshot document : task.getResult()) {
                            String analysisResult = document.getString("analysisResult");
                            float confidence = extractConfidenceClass(analysisResult);
                            String predictedClass = extractPredictedClass(analysisResult);

                            Date date = document.getDate("timestamp"); // Use getDate method directly

                            Entry entry = new Entry(xValues.indexOf(predictedClass), confidence);
                            switch (predictedClass) {
                                case "dark_circles":
                                    darkCirclesEntries.add(entry);
                                    break;
                                case "enlarged_pores":
                                    enlargedPoresEntries.add(entry);
                                    break;
                                case "pimples":
                                    pimplesEntries.add(entry);
                                    break;
                                case "hyperpigmentation":
                                    hyperpigmentationEntries.add(entry);
                                    break;
                                case "wrinkles":
                                    wrinklesEntries.add(entry);
                                    break;
                                case "fine_lines":
                                    fineLinesEntries.add(entry);
                                    break;
                            }
                        }
                        plotClassEntries("Dark Circles", darkCirclesEntries);
                        plotClassEntries("Enlarged Pores", enlargedPoresEntries);
                        plotClassEntries("Pimples", pimplesEntries);
                        plotClassEntries("Hyperpigmentation", hyperpigmentationEntries);
                        plotClassEntries("Wrinkles", wrinklesEntries);
                        plotClassEntries("Fine Lines", fineLinesEntries);

                    } else {
                        // Handle error
                    }
                });
    }

    private float extractConfidenceClass(String analysisResult) {
        String[] parts = analysisResult.split("Confidence: ");
        if (parts.length > 1) {
            String confidenceStr = parts[1].split(",")[0].trim();
            return Float.parseFloat(confidenceStr);
        }
        return 0;
    }

    private void plotClassEntries(String className, List<Entry> entries) {
        if (!entries.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(entries, className);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate(); // Refresh chart
        }
    }

    private String extractPredictedClass(String analysisResult) {
        String[] parts = analysisResult.split("Predicted Class: ");
        if (parts.length > 1) {
            String classAndConfidence = parts[1].trim();
            return classAndConfidence.split(",")[0].trim();
        }
        return "";
    }
}