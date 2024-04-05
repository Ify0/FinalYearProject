package com.example.finalyear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class Tracker {
    private FirebaseFirestore firestoreDB;
    private CollectionReference uploadsRef;
    private LineChart lineChart;
    private Context context;
    private FirebaseUser currentUser;

    public Tracker(Context context, LineChart lineChart) {
        this.context = context;
        this.lineChart = lineChart;

        // Retrieve the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            firestoreDB = FirebaseFirestore.getInstance();
            // Adjust the reference to match your database structure
            uploadsRef = firestoreDB.collection("Users")
                    .document(currentUser.getUid())
                    .collection("uploads");
        } else {
            // Handle the case where the current user is null (not authenticated)
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


    public void trackSkinAnalysisProgress() {
        uploadsRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Entry> entries = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String analysisResult = document.getString("analysisResult");
                        double confidence = extractConfidenceValue(analysisResult);
                        String imageUrl = document.getString("imageUrl");

                        // Fetch the last modified timestamp of the file from Firebase Storage
                        getFileLastModifiedTimestamp(imageUrl, new TimestampCallback() {
                            @Override
                            public void onTimestampReceived(long storageTimestamp) {
                                // Extract percentage from confidence
                                float percentage = (float) (confidence * 100.0);

                                // Add entry for the chart
                                entries.add(new Entry(storageTimestamp, percentage));

                                // Check if all entries are fetched before updating the chart
                                if (entries.size() == queryDocumentSnapshots.size()) {
                                    updateChart(entries);
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                });
    }



    // Interface to receive the last modified timestamp of the file
    interface TimestampCallback {
        void onTimestampReceived(long storageTimestamp);
    }

    // Function to fetch the last modified timestamp of the file from Firebase Storage
    private void getFileLastModifiedTimestamp(String imageUrl, TimestampCallback callback) {
        // Get a reference to the Firebase Storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Extract the path from the imageUrl
        String imagePath = getImagePathFromUrl(imageUrl);

        // Create a reference to the file in Firebase Storage
        StorageReference fileRef = storage.getReference().child(imagePath);

        // Get metadata for the file to fetch the last modified timestamp
        fileRef.getMetadata()
                .addOnSuccessListener(storageMetadata -> {
                    // Get the last modified timestamp in milliseconds
                    long lastModifiedTimestamp = storageMetadata.getUpdatedTimeMillis();

                    // Call the callback with the timestamp value
                    callback.onTimestampReceived(lastModifiedTimestamp);
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch metadata
                    callback.onTimestampReceived(0); // Return default timestamp or handle error
                });
    }



    // Function to extract the path from the imageUrl
    private String getImagePathFromUrl(String imageUrl) {
        // Example: https://firebasestorage.googleapis.com/v0/b/database-ede31.appspot.com/o/uploads%2F1705527435097.jpg?alt=media&token=e6dc0a2d-04a0-4f88-84c8-73e19b644
        // Extract the path after "uploads%2F" and before "?alt"
        int startIndex = imageUrl.indexOf("uploads%2F") + "uploads%2F".length();
        int endIndex = imageUrl.indexOf("?alt");
        return imageUrl.substring(startIndex, endIndex);
    }


    // Function to update the chart with the entries
    private void updateChart(ArrayList<Entry> entries) {
        // Create a dataset with the entries
        LineDataSet dataSet = new LineDataSet(entries, "Skin Analysis Progress");
        // Set labels for the dataset
        dataSet.setLabel("Skin Analysis Progress");

        // Create a LineData object from the dataset
        LineData lineData = new LineData(dataSet);

        // Set the LineData to the chart and refresh it
        lineChart.setData(lineData);
        lineChart.invalidate();

        // Customize X-axis and Y-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Convert timestamp value to date string
                return new Date((long) value).toString();
            }
        });
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMaximum(100f);
        lineChart.getAxisRight().setAxisMinimum(0f);
        lineChart.getAxisRight().setAxisMaximum(100f);
    }


    // Function to extract confidence value from analysisResult string
    private double extractConfidenceValue(String analysisResult) {
        // Split the analysisResult string by commas
        String[] parts = analysisResult.split(", ");

        // Iterate over the parts to find the one containing "Confidence"
        for (String part : parts) {
            // Check if the part contains "Confidence"
            if (part.startsWith("Confidence: ")) {
                // Extract the confidence value (substring after "Confidence: ")
                String confidenceString = part.substring("Confidence: ".length());
                // Parse the confidence value to double and return
                return Double.parseDouble(confidenceString);
            }
        }
        // Return default value or handle error as needed
        return 0.0; // Default value
    }
}
