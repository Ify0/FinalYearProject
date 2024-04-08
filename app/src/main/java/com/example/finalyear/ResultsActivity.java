package com.example.finalyear;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ResultsActivity extends AppCompatActivity {

    private ImageView resultImageView;
    private TextView mainPriorityValueTextView;
    private TextView likelyTextView;
    private TextView selectionMainPriorityValueTextView;
    private Button discoverButton;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private String predictedClass, confidence, mainPriorityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        discoverButton = findViewById(R.id.discoverButton);
        resultImageView = findViewById(R.id.resultImageView);
        mainPriorityValueTextView = findViewById(R.id.mainPriorityValueTextView);
        likelyTextView = findViewById(R.id.likelyTextView);
        selectionMainPriorityValueTextView = findViewById(R.id.selectionMainPriorityValueTextView);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        if (currentUser != null) {
            retrieveImage();
           // retrieveAnalysisResult();
           // retrieveMainPriorityValue();
        }
        retrieveAnalysisResult();
        retrieveMainPriorityValue();

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, RoutineActivity.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveImage() {
        // Firestore reference for the user's dynamically changing image path
        db.collection("Users")
                .document(currentUser.getUid())
                .collection("uploads")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String imagePath = document.getString("imageUrl");

                            // Directly pass the imageUrl to Glide for loading
                            loadImage(imagePath);

                            // Assuming there's only one image path, break after the first iteration
                            break;
                        }
                    }
                });
    }

    private void loadImage(String imageUrl) {
        // Get the download URL
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);

        // Load image into ImageView using Glide
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .into(resultImageView);
        }).addOnFailureListener(e -> {
            // Handle any errors during the download
            e.printStackTrace();
        });
    }

    private void retrieveAnalysisResult() {
        // Firestore reference for the user's analysis result
        db.collection("Users")
                .document(currentUser.getUid())
                .collection("uploads")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String analysisResult = document.getString("analysisResult");
                            // Extract the predicted class value
                            predictedClass = extractPredictedClass(analysisResult);
                            confidence = extractConfidenceClass(analysisResult);

                            // Update UI element
                            mainPriorityValueTextView.setText(predictedClass);
                            likelyTextView.setText(confidence);

                            // Assuming there's only one analysis result, break after the first iteration
                            break;
                        }
                    } else {
                        // Handle errors
                        Log.e(TAG, "Error getting analysis result: ", task.getException());
                    }
                });
    }

    private void retrieveMainPriorityValue() {
        // Firestore reference for the user's main priority value
        db.collection("Users")
                .document(currentUser.getUid())
                .collection("Report")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            mainPriorityValue = document.getString("priority");

                            // Update UI element
                            selectionMainPriorityValueTextView.setText(mainPriorityValue);

                            // Assuming there's only one main priority value, break after the first iteration
                            break;
                        }
                    } else {
                        // Handle errors
                        Log.e(TAG, "Error getting main priority value: ", task.getException());
                    }
                });
    }

    private String extractPredictedClass(String analysisResult) {
        // Extracting the predicted class value from the analysis result string
        // This is a simple example. You may need to implement a proper parsing logic based on your actual data structure.
        String[] parts = analysisResult.split("Predicted Class: ");
        if (parts.length > 1) {
            return parts[1].split(",")[0].trim();
        }
        return "";
    }

    private String extractConfidenceClass(String analysisResult) {
        // Extracting the predicted class value from the analysis result string
        // This is a simple example. You may need to implement a proper parsing logic based on your actual data structure.
        String[] parts = analysisResult.split("Confidence: ");
        if (parts.length > 1) {
            String predictedValueStr = parts[1].split(",")[0].trim();

            try {
                double predictedValue = Double.parseDouble(predictedValueStr);

                if (predictedValue > 0.70) {
                    return "Certain";
                } else if (predictedValue > 0.40) {
                    return "Highly Likely";
                } else if (predictedValue > 0.0) {
                    return "Likely";
                } else {
                    // Add any additional conditions as needed
                    return "Unknown";
                }
            } catch (NumberFormatException e) {
                // Handle non-numeric values, e.g., "hyperpigmentation"
                return "Unknown";
            }
        }
        return "";
    }
}
