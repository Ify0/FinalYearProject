package com.example.finalyear;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackerActivity extends AppCompatActivity {

    private LineChart lineChart;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

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
                       // List<Entry> entries = new ArrayList<>();
                        List<Entry> darkCirclesEntries = new ArrayList<>();
                        List<Entry> enlargedPoresEntries = new ArrayList<>();
                        List<Entry> pimplesEntries = new ArrayList<>();
                        List<Entry> hyperpigmentationEntries = new ArrayList<>();
                        List<Entry> wrinklesEntries = new ArrayList<>();
                        List<Entry> fineLinesEntries = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String analysisResult = document.getString("analysisResult");
                            float confidence = extractConfidenceClass(analysisResult);
                            String predictedClass = extractPredictedClass(analysisResult);
                            // Handle timestamp field appropriately
                            Date date;
                            Object timestampObj = document.get("timestamp");
                            if (timestampObj instanceof com.google.firebase.Timestamp) {
                                com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) timestampObj;
                                date = timestamp.toDate();
                            } else {
                                // Handle other types or null values appropriately
                                date = new Date(); // Default to current date
                            }

                           //entries.add(new Entry(date.getTime(), confidence));

                            Entry entry = new Entry(date.getTime(), confidence);
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
                       // setupLineChart(entries);
                        // Plot the entries for each class
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
    private void setupLineChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Confidence Progress");
        LineData lineData = new LineData(dataSet);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DateAxisValueFormatter());

        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh chart
    }

    private static class DateAxisValueFormatter extends ValueFormatter {
        private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            long millis = (long) value;
            return dateFormat.format(new Date(millis));
        }
    }
}
