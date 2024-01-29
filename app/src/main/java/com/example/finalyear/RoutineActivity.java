package com.example.finalyear;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RoutineActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private RecyclerView recyclerViewCleanser;
    private CleanserAdapter cleanserAdapter;
    private FirebaseUser currentUser;
    private List<Product> filteredCleansers; // Adjust based on your actual data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        // Initialize Firebase Storage, Firestore, and RecyclerView
        initializeFirebase();
        initializeUI();

        // Retrieve user preferences from Firestore
        retrieveUserPreferencesFromFirestore();

        // Retrieve the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in
            // Retrieve user preferences from Firestore
            retrieveUserPreferencesFromFirestore();
        } else {
            // User is not signed in, handle accordingly
            Log.d("Firestore", "User is not signed in.");
        }

        // Set onClickListener for the "SEND ROUTINE" button
        Button sendRoutineButton = findViewById(R.id.sendRoutineButton);
        sendRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click event
            }
        });
    }

    private void initializeFirebase() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void initializeUI() {
        recyclerViewCleanser = findViewById(R.id.recyclerViewCleanser);
        // Initialize and set up the adapter
        cleanserAdapter = new CleanserAdapter(this, new ArrayList<>());
        recyclerViewCleanser.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCleanser.setAdapter(cleanserAdapter);
    }

    private void retrieveUserPreferencesFromFirestore() {
        firestore.collection("Users")
                .document(currentUser.getUid())
                .collection("Report")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Assuming you only expect one document, you can get the first one
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            if (document != null && document.exists()) {
                                // Retrieve user preferences and proceed
                                UserPreferences userPreferences = document.toObject(UserPreferences.class);
                                if (userPreferences != null) {
                                    handleUserPreferences(userPreferences);
                                }
                            } else {
                                // Handle the case where the document does not exist
                                Log.d("Firestore", "No document found in the 'Report' collection for the user.");
                            }
                        } else {
                            // Handle the case where no documents are found
                            Log.d("Firestore", "No documents found in the 'Report' collection for the user.");
                        }
                    } else {
                        // Handle other errors during the Firestore query
                        Log.e("Firestore", "Error fetching documents from the 'Report' collection.", task.getException());
                    }
                });
    }




    private void handleUserPreferences(UserPreferences userPreferences) {
        // Download JSON files and filter products
        downloadJsonFile("/cleansers/DryCleanser.json", userPreferences, recyclerViewCleanser);
        // You can repeat this for other sections (treatments, moisturizers)
        downloadJsonFile("cleansers/CombinationCleanser.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("/cleansers/NormalSkinCleanser.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("cleansers/OilyCleanser.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("moisturiser/Comb/FineLine&WrinklesCombMoist.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("moisturiser/Comb/Pimples&PigmentationMOISTComb.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("/moisturiser/Dry/Fineline&WrinklesMoisturerDry.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("gs://database-ede31.appspot.com/moisturiser/Dry/Pimples&PigmentationtreatDry.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("gs://database-ede31.appspot.com/moisturiser/NormalSkin/FineLine&WrinkesMoistNS.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("gs://database-ede31.appspot.com/moisturiser/NormalSkin/Pimples&PigmentationMoistNS.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("gs://database-ede31.appspot.com/moisturiser/Oliy/Pimples&PigmentationMoistOliy.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("gs://database-ede31.appspot.com/moisturiser/Oliy/Wrinkles&FineLinesMoistOily.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("gs://database-ede31.appspot.com/treatment/comb/FineLine&WrinklesCombTreat.json", userPreferences, recyclerViewCleanser);
        downloadJsonFile("gs://database-ede31.appspot.com/treatment/comb/Pimples&PigmentationTREATComb.json", userPreferences, recyclerViewCleanser);
    }

    private void downloadJsonFile(String filePath, UserPreferences userPreferences, RecyclerView recyclerView) {
        StorageReference storageRef = storage.getReference().child(filePath);
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Download the file and parse JSON
                    InputStream inputStream = null;
                    try {
                        Uri downloadUrl = task.getResult();
                        inputStream = new URL(downloadUrl.toString()).openStream();
                        String jsonString = readStream(inputStream);

                        // Parse JSON and filter products
                        filteredCleansers = parseAndFilterProducts(jsonString, userPreferences);

                        // Display filtered products in RecyclerView
                        displayProductsInRecyclerView(filteredCleansers, recyclerView);
                    } catch (IOException e) {
                        // Handle error
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                // Handle error
                            }
                        }
                    }
                } else {
                    // Handle error
                }
            }
        });
    }

    private String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private List<Product> parseAndFilterProducts(String jsonString, UserPreferences userPreferences) {
        List<Product> products = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            // Parse price range from user preferences
            String[] priceRangeStrings = userPreferences.getPriceRange().split("-");
            double minPrice = Double.parseDouble(priceRangeStrings[0]);
            double maxPrice = Double.parseDouble(priceRangeStrings[1]);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productJson = jsonArray.getJSONObject(i);

                String name = productJson.getString("name");
                double price = productJson.getDouble("price");

                // Filter products based on price range
                if (price >= minPrice && price <= maxPrice) {
                    // Create new Product and add to list
                    Product product = new Product();
                    product.setName(name);
                    product.setPrice(price);
                    products.add(product);
                }
            }
        } catch (JSONException e) {
            // Handle JSON parsing error
            e.printStackTrace();
        }

        return products;
    }

    private void displayProductsInRecyclerView(List<Product> products, RecyclerView recyclerView) {
        // Update the data in the adapter and notify it of the change
        cleanserAdapter.setCleanserList(products);
        cleanserAdapter.notifyDataSetChanged();
    }
}
