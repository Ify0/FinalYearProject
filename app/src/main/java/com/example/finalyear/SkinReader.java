package com.example.finalyear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SkinReader extends AppCompatActivity {

    private RadioGroup skinTypeRadioGroup;
    private Spinner prioritySpinner;
    private RadioGroup priceRangeRadioGroup;
    private ImageButton saveButton;

    private FirebaseFirestore firestore;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skin_profile_activity);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        skinTypeRadioGroup = findViewById(R.id.radioGroup);
        prioritySpinner = findViewById(R.id.spinner);
        priceRangeRadioGroup = findViewById(R.id.radioGroupPrice);
        saveButton = findViewById(R.id.imageButton);

        // Set up the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        // Set up button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataToFirestore();
                Intent intent = new Intent(SkinReader.this, ImageScanner.class);
                startActivity(intent);
            }
        });
    }

    private void saveDataToFirestore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        // Gather selected values
        int selectedSkinTypeId = skinTypeRadioGroup.getCheckedRadioButtonId();
        int selectedPriceRangeId = priceRangeRadioGroup.getCheckedRadioButtonId();
        String selectedPriority = prioritySpinner.getSelectedItem().toString();

        if (selectedSkinTypeId == -1 || selectedPriceRangeId == -1) {
            // Handle case when not all options are selected
            Toast.makeText(this, "Please select skin type and price range", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Report object to store the selected values
        String userId = user.getUid();
        Report profile = new Report();
        profile.setOily(selectedSkinTypeId == R.id.rb_oily);
        profile.setDry(selectedSkinTypeId == R.id.rb_dry);
        profile.setCombination(selectedSkinTypeId == R.id.rb_combination);
        profile.setNormalSkin(selectedSkinTypeId == R.id.rb_normal);
        profile.setPriceRange(getPriceRangeFromId(selectedPriceRangeId));
        profile.setPriority(selectedPriority);

        // Add the data to Firestore
        CollectionReference userReportRef = db.collection("Users").document(userId).collection("Report");
        userReportRef
                .add(profile)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SkinReader.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SkinReader.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private String getPriceRangeFromId(int id) {
        if (id == R.id.rb_range1) {
            return "10 - 30";
        } else if (id == R.id.rb_range2) {
            return "30 - 50";
        } else if (id == R.id.rb_range3) {
            return "50 - 100";
        } else {
            return "";
        }
    }
    }


