package com.example.finalyear;

import static java.io.File.createTempFile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RoutineActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private RecyclerView recyclerViewCleanser;
    private RecyclerView recyclerViewTreatment;
    private RecyclerView recyclerViewMoisture;
    private TreatmentAdapter treatmentAdapter;
    private CleanserAdapter cleanserAdapter;
    private MoistureAdapter moistureAdapter;
    private FirebaseUser currentUser;
    private List<Product> filteredProducts;

    private UserPreferences userPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        // Initialize Firebase Storage, Firestore, and RecyclerView
        initializeFirebase();
        initializeUI();

        // Retrieve the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in
            // Retrieve user preferences from Firestore
            retrieveUserPreferencesFromFirestore();
           // retrieveAnalysisResult();
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
                showEmailDialog();
            }
        });
    }

    private void initializeFirebase() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void initializeUI() {
        recyclerViewCleanser = findViewById(R.id.recyclerViewCleanser);
        recyclerViewTreatment = findViewById(R.id.recyclerViewTreatments);
        recyclerViewMoisture = findViewById(R.id.recyclerViewMoisture);

        cleanserAdapter = new CleanserAdapter(this, new ArrayList<>());
        treatmentAdapter = new TreatmentAdapter(this, new ArrayList<>());
        moistureAdapter = new MoistureAdapter(this, new ArrayList<>());

        recyclerViewCleanser.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTreatment.setLayoutManager(new LinearLayoutManager(this)); // Uncomment this line
        recyclerViewMoisture.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewCleanser.setAdapter(cleanserAdapter);
        recyclerViewTreatment.setAdapter(treatmentAdapter);
        recyclerViewMoisture.setAdapter(moistureAdapter);
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
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            if (document != null && document.exists()) {
                                UserPreferences userPreferences = document.toObject(UserPreferences.class);
                                handleUserSkinType(userPreferences);
                                if (userPreferences != null) {
                                    handleUserPreferences(userPreferences);
                                    retrieveAnalysisResult();
                                }
                            } else {
                                Log.d("Firestore", "No document found in the 'Report' collection for the user.");
                            }
                        } else {
                            Log.d("Firestore", "No documents found in the 'Report' collection for the user.");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching documents from the 'Report' collection.", task.getException());
                    }
                });
    }

    private void handleUserSkinType(UserPreferences userPreferences) {
        String skinType;
        this.userPreferences = userPreferences;
        if (userPreferences.isDry()) {
            skinType = "dry";
        } else if (userPreferences.isNormalSkin()) {
            skinType = "normalSkin";
        } else if (userPreferences.isOily()) {
            skinType = "oily";
        } else if (userPreferences.isCombination()) {
            skinType = "combination";
        } else {
            skinType = "unknown";
        }

        Log.d("Firestore", "User skin type: " + skinType);
    }

    private void retrieveAnalysisResult() {
        firestore.collection("Users")
                .document(currentUser.getUid())
                .collection("uploads")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("Firestore", "Processing document: " + document.getId());
                                if (document.exists()) {
                                    String analysisResult = document.getString("analysisResult");
                                    Log.d("Firestore", "Raw Analysis Result: " + analysisResult);
                                    analysisResult = extractPredictedClass(analysisResult);
                                    if (analysisResult != null) {
                                        handleAnaylsisResults(analysisResult, userPreferences);
                                        Log.d("AnalysisResult", "Predicted Class: " + analysisResult);
                                        break;
                                    } else {
                                        Log.e("Firestore", "Error parsing AnalysisResultRoutine or PredictedClass is null");
                                    }
                                } else {
                                    Log.e("Firestore", "Document does not exist in the 'uploads' collection.");
                                }
                            }
                        } else {
                            Log.e("Firestore", "No documents found in the 'uploads' collection.");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching documents from the 'uploads' collection.", task.getException());
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
    private void handleAnaylsisResults(String result , UserPreferences userPreferences) {

        if (userPreferences.isDry()) {
            if (result.contains("wrinkles")) {
                downloadJsonFile("/treatment/dry/Fineline&WrinklesTreatDry.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("fine_lines")) {
                downloadJsonFile("/treatment/dry/Fineline&WrinklesTreatDry.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("hyperpigmentation")) {
                downloadJsonFile("/treatment/dry/Pimples&PigmentationtreatDry.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("pimples")) {
                downloadJsonFile("/treatment/dry/Pimples&PigmentationtreatDry.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("enlarged_pores")) {
                downloadJsonFile("/treatment/dry/dry_skin_products_pores.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("dark_circles")) {
                downloadJsonFile("/treatment/darkcircles/darkcicles.json", userPreferences, recyclerViewTreatment);
            }
        } else if (userPreferences.isNormalSkin()) {
            if (result.contains("wrinkles")) {
                downloadJsonFile("/treatment/normalSkin/FineLine&WrinkesTreatNS.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("fine_lines")) {
                downloadJsonFile("/treatment/normalSkin/FineLine&WrinkesTreatNS.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("hyperpigmentation")) {
                downloadJsonFile("/treatment/normalSkin/Pimples&PigmentationTREATNS.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("pimples")) {
                downloadJsonFile("/treatment/normalSkin/Pimples&PigmentationTREATNS.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("enlarged_pores")) {
                downloadJsonFile("/treatment/normalSkin/normal_skin_products_pores.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("dark_circles")) {
                downloadJsonFile("/treatment/darkcircles/darkcicles.json", userPreferences, recyclerViewTreatment);
            }
        } else if (userPreferences.isOily()) {
            if (result.contains("wrinkles")) {
                downloadJsonFile("/treatment/oily/Pimples&PigmentationTreatOliy.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("fine_lines")) {
                downloadJsonFile("/treatment/oily/Pimples&PigmentationTreatOliy.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("hyperpigmentation")) {
                downloadJsonFile("/treatment/oily/Pimples&PigmentationTreatOliy.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("pimples")) {
                downloadJsonFile("/treatment/oily/Pimples&PigmentationTreatOliy.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("enlarged_pores")) {
                downloadJsonFile("/treatment/oily/oily_skin_products_pores.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("dark_circles")) {
                downloadJsonFile("/treatment/darkcircles/darkcicles.json", userPreferences, recyclerViewTreatment);
            }
        }

        if (userPreferences.isCombination()) {
            if (result.contains("wrinkles")) {
                downloadJsonFile("/treatment/comb/FineLine&WrinklesCombTreat.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("fine_lines")) {
                downloadJsonFile("/treatment/comb/FineLine&WrinklesCombTreat.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("hyperpigmentation")) {
                downloadJsonFile("/treatment/comb/Pimples&PigmentationTREATComb.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("pimples")) {
                downloadJsonFile("/treatment/comb/Pimples&PigmentationTREATComb.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("enlarged_pores")) {
                downloadJsonFile("/treatment/comb/combination_skin_products_pores.json", userPreferences, recyclerViewTreatment);
            } else if (result.contains("dark_circles")) {
                downloadJsonFile("/treatment/darkcircles/darkcicles.json", userPreferences, recyclerViewTreatment);
            }
        }
    }

    private void handleUserPreferences(UserPreferences userPreferences) {
        if (userPreferences.isDry()) {
            downloadJsonFile("/cleansers/DryCleanser.json", userPreferences, recyclerViewCleanser);
            if (userPreferences.getPriority().equals("Wrinkles")) {
                downloadJsonFile("/moisturiser/Dry/Fineline&WrinklesMoisturerDry.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Fine Lines")) {
                downloadJsonFile("/moisturiser/Dry/Fineline&WrinklesMoisturerDry.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Hyperpigmentation")) {
                downloadJsonFile("/moisturiser/Dry/Pimples&PigmentationtreatDry.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Pimples")) {
                downloadJsonFile("/moisturiser/Dry/Pimples&PigmentationtreatDry.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Enlarged Pores")) {
            } else if (userPreferences.getPriority().equals("Dark Circles")) {

            }
        } else if (userPreferences.isNormalSkin()) {
            downloadJsonFile("/cleansers/NormalSkinCleanser.json", userPreferences, recyclerViewCleanser);
            if (userPreferences.getPriority().equals("Wrinkles")) {
                downloadJsonFile("/moisturiser/NormalSkin/FineLine&WrinkesMoistNS.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Fine Lines")) {
                downloadJsonFile("/moisturiser/NormalSkin/FineLine&WrinkesMoistNS.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Hyperpigmentation")) {
                downloadJsonFile("/moisturiser/NormalSkin/Pimples&PigmentationMoistNS.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Pimples")) {
                downloadJsonFile("/moisturiser/NormalSkin/Pimples&PigmentationMoistNS.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Enlarged Pores")) {
            } else if (userPreferences.getPriority().equals("Dark Circles")) {

            }
        } else if (userPreferences.isOily()) {
            downloadJsonFile("/cleansers/OilyCleanser.json", userPreferences, recyclerViewCleanser);
            if (userPreferences.getPriority().equals("Wrinkles")) {
                downloadJsonFile("/moisturiser/Oliy/Wrinkles&FineLinesMoistOily.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Fine Lines")) {
                downloadJsonFile("/moisturiser/Oliy/Wrinkles&FineLinesMoistOily.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Hyperpigmentation")) {
                downloadJsonFile("/moisturiser/Oliy/Pimples&PigmentationMoistOliy.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Pimples")) {
                downloadJsonFile("/moisturiser/Oliy/Pimples&PigmentationMoistOliy.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Enlarged Pores")) {
            } else if (userPreferences.getPriority().equals("Dark Circles")) {
            }
        }
        if (userPreferences.isCombination()) {
            downloadJsonFile("/cleansers/CombinationCleanser.json", userPreferences, recyclerViewCleanser);
            if (userPreferences.getPriority().equals("Wrinkles")) {
                downloadJsonFile("/moisturiser/Comb/FineLine&WrinklesCombMoist.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Fine Lines")) {
                downloadJsonFile("/moisturiser/Comb/FineLine&WrinklesCombMoist.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Hyperpigmentation")) {
                downloadJsonFile("/moisturiser/NormalSkin/Pimples&PigmentationMoistNS.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Pimples")) {
                downloadJsonFile("/moisturiser/NormalSkin/Pimples&PigmentationMoistNS.json", userPreferences, recyclerViewMoisture);
            } else if (userPreferences.getPriority().equals("Enlarged Pores")) {
            } else if (userPreferences.getPriority().equals("Dark Circles")) {
            }
        }

        Log.d("Firestore", "User Cleanser: " + recyclerViewCleanser);
    }

    private void downloadJsonFile(String filePath, UserPreferences userPreferences, RecyclerView recyclerView) {
        StorageReference storageRef = storage.getReference().child(filePath);
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    if (downloadUrl != null) {
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                String jsonString = null;
                                try {
                                    InputStream inputStream = new URL(downloadUrl.toString()).openStream();
                                    jsonString = readStream(inputStream);
                                    // Call the modified sendEmailNow method
                                    //   saveToTxtFile(jsonString, "example.txt", "user@example.com", "Email Content here");
                                    //  sendEmailNow("user@example.com", "Email Content here", downloadUrl.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return jsonString;
                            }

                            @Override
                            protected void onPostExecute(String jsonString) {
                                super.onPostExecute(jsonString);
                                if (jsonString != null) {
                                    filteredProducts = parseAndFilterProducts(jsonString, userPreferences);
                                    displayProductsInRecyclerView(filteredProducts, recyclerView);
                                } else {
                                    // Handle parsing error
                                }
                            }
                        }.execute();
                    }
                } else {
                    // Handle error
                }
            }

        });
    }
  // private void saveToTxtFile(String content, String fileName, String emailAddress, String emailContent) throws IOException {
  //     try {
  //         File file = new File(getFilesDir(), fileName);
  //         FileWriter fileWriter = new FileWriter(file);
  //         fileWriter.write(content);
  //         fileWriter.flush();
  //         fileWriter.close();
  //         Log.d("File", "File saved at: " + file.getAbsolutePath());

  //         // Upload the file to Firebase Storage
  //         StorageReference storageRef = storage.getReference().child("files/" + fileName);

  //         try {
  //             InputStream stream = new ByteArrayInputStream(content.getBytes()); // Convert your text to InputStream
  //             UploadTask uploadTask = storageRef.putStream(stream);

  //             uploadTask.addOnSuccessListener(taskSnapshot -> {
  //                 // File uploaded successfully
  //                 // Get the download URL and pass it to the sendEmailNow method
  //                 String downloadUrl = getDownloadUrl(taskSnapshot);
  //                 if (downloadUrl != null) {
  //                     try {
  //                         sendEmailNow(emailAddress, emailContent, content); // Pass content for attachment
  //                     } catch (IOException e) {
  //                         throw new RuntimeException(e);
  //                     }
  //                 } else {
  //                     // Handle the case where the download URL is not available
  //                     Log.e("FirebaseStorage", "Download URL is null");
  //                 }
  //             }).addOnFailureListener(exception -> {
  //                 // Handle unsuccessful uploads
  //                 Log.e("FirebaseStorage", "Error uploading file", exception);
  //             });
  //         } catch (Exception e) {
  //             // Handle exception
  //             Log.e("FirebaseStorage", "Error converting text to InputStream", e);
  //         }
  //     } catch (IOException e) {
  //         e.printStackTrace();
  //         Log.e("File", "Error saving file: " + e.getMessage());
  //         throw e; // Rethrow the IOException
  //     }
  // }


    private void downloadJsonFile(String filePath, AnalysisResultRoutine result, RecyclerView recyclerView) {
        StorageReference storageRef = storage.getReference().child(filePath);
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    if (downloadUrl != null) {
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                try {
                                    InputStream inputStream = new URL(downloadUrl.toString()).openStream();
                                    return readStream(inputStream);
                                } catch (IOException e) {
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(String jsonString) {
                                super.onPostExecute(jsonString);
                                if (jsonString != null) {
                                    filteredProducts = parseAndFilterProducts(jsonString, result);
                                    displayProductsInRecyclerView(filteredProducts, recyclerView);
                                } else {
                                    // Handle parsing error
                                }
                            }
                        }.execute();
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

            String[] priceRangeStrings = userPreferences.getPriceRange().split("-");
            double minPrice = Double.parseDouble(priceRangeStrings[0]);
            double maxPrice = Double.parseDouble(priceRangeStrings[1]);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productJson = jsonArray.getJSONObject(i);

                String name = productJson.getString("name");
                double price = productJson.getDouble("price");

                if (price >= minPrice && price <= maxPrice) {
                    Product product = new Product();
                    product.setName(name);
                    product.setPrice(price);
                    products.add(product);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return products;
    }
    private List<Product> parseAndFilterProducts(String jsonString, AnalysisResultRoutine result) {
        List<Product> products = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            String[] priceRangeStrings = result.getPriceRange().split("-");
            double minPrice = Double.parseDouble(priceRangeStrings[0]);
            double maxPrice = Double.parseDouble(priceRangeStrings[1]);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productJson = jsonArray.getJSONObject(i);

                String name = productJson.getString("name");
                double price = productJson.getDouble("price");

                if (price >= minPrice && price <= maxPrice) {
                    Product product = new Product();
                    product.setName(name);
                    product.setPrice(price);
                    products.add(product);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return products;
    }
    private void displayProductsInRecyclerView(List<Product> products, RecyclerView recyclerView) {
        if (recyclerView == recyclerViewCleanser) {
            cleanserAdapter.setCleanserList(products);
            cleanserAdapter.notifyDataSetChanged();
        } else if (recyclerView == recyclerViewTreatment) {
            treatmentAdapter.setTreatmentList(products);
            treatmentAdapter.notifyDataSetChanged();
        } else if (recyclerView == recyclerViewMoisture) {
            moistureAdapter.setMoistureList(products);
            moistureAdapter.notifyDataSetChanged();
        }
    }
 //  private void saveProductsToFirestore(List<Product> products, String userId) {
 //      // Create a new collection in Firestore for storing products
 //      CollectionReference productsCollection = firestore.collection("users").document(userId).collection("products");

 //      // Iterate through the list of products and store each product in Firestore
 //      for (Product product : products) {
 //          Map<String, Object> productData = new HashMap<>();
 //          productData.put("name", product.getName());
 //          productData.put("price", product.getPrice());

 //          // Generate a unique ID for each product
 //          String productId = UUID.randomUUID().toString();

 //          // Store the product data in Firestore with the generated ID
 //          productsCollection.document(productId).set(productData)
 //                  .addOnSuccessListener(new OnSuccessListener<Void>() {
 //                      @Override
 //                      public void onSuccess(Void aVoid) {
 //                          Log.d("Firestore", "Product saved successfully: " + product.getName());
 //                      }
 //                  })
 //                  .addOnFailureListener(new OnFailureListener() {
 //                      @Override
 //                      public void onFailure(@NonNull Exception e) {
 //                          Log.e("Firestore", "Error saving product: " + product.getName(), e);
 //                      }
 //                  });
 //      }
 //  }

 //  private void showEmailDialog() {
 //      // Inflate the layout for the dialog
 //      View view = LayoutInflater.from(this).inflate(R.layout.dialog_email_input, null);

 //      // Find the email EditText and initialize it
 //      EditText emailEditText = view.findViewById(R.id.emailEditText);

 //      // Create the AlertDialog
 //      AlertDialog.Builder builder = new AlertDialog.Builder(this);
 //      builder.setView(view)
 //              .setTitle("Enter Your Email")
 //              .setPositiveButton("Send", new DialogInterface.OnClickListener() {
 //                  @Override
 //                  public void onClick(DialogInterface dialog, int which) {
 //                      String emailAddress = emailEditText.getText().toString().trim();

 //                      // Call this after obtaining filtered products in parseAndFilterProducts methods
 //                      saveProductsToFirestore(filteredProducts, currentUser.getUid());

 //                      // Store email and contents on Firebase before sending
 //                      storeEmailOnFirebase(emailAddress, filteredProducts);

 //                      // Convert filteredProducts to email content string
 //                      String emailContent = convertProductsToEmailContent(filteredProducts);

 //                      //sample
 //                      String test = " Testing this";
 //                      // Retrieve the download URL asynchronously
 //                      try {
 //                          sendEmailNow(emailAddress, emailContent, test);
 //                      } catch (IOException e) {
 //                          throw new RuntimeException(e);
 //                      }
 //                  }
 //              });

 //      // Show the dialog
 //      AlertDialog dialog = builder.create();
 //      dialog.show();
 //  }

 //  // Define an interface for the callback
 //  interface OnDownloadUrlCallback {
 //      void onUrlReady(String downloadUrl);

 //      void onUrlError();
 //  }

 //  private void storeEmailOnFirebase(String emailAddress, List<Product> products) {
 //      // Create a new document in Firestore for storing email and products
 //      DocumentReference emailDocRef = firestore.collection("emails").document();

 //      // Create a Map to store the email and products data
 //      Map<String, Object> emailData = new HashMap<>();
 //      emailData.put("email", emailAddress);

 //      // Convert the list of products to a List of Map<String, Object> for Firestore
 //      List<Map<String, Object>> productsData = new ArrayList<>();
 //      for (Product product : products) {
 //          Map<String, Object> productData = new HashMap<>();
 //          productData.put("name", product.getName());
 //          productData.put("price", product.getPrice());
 //          productsData.add(productData);
 //      }
 //      emailData.put("products", productsData);

 //      // Store the email and products data in Firestore
 //      emailDocRef.set(emailData)
 //              .addOnSuccessListener(new OnSuccessListener<Void>() {
 //                  @Override
 //                  public void onSuccess(Void aVoid) {
 //                      Log.d("Firestore", "Email and products saved successfully");
 //                  }
 //              })
 //              .addOnFailureListener(new OnFailureListener() {
 //                  @Override
 //                  public void onFailure(@NonNull Exception e) {
 //                      Log.e("Firestore", "Error saving email and products", e);
 //                  }
 //              });
 //  }


 //  private void sendEmailNow(String emailAddress, String emailContent, String attachmentContent) throws IOException {
 //      // Create a temporary text file and write the content to it
 //      File tempFile = createTempFile("routine", ".txt");

 //      try {
 //          FileWriter writer = new FileWriter(tempFile);
 //          writer.write(attachmentContent);
 //          writer.close();
 //      } catch (IOException e) {
 //          e.printStackTrace();
 //      }

 //      // Attach the text file to the email
 //      Uri attachmentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + "com.example.finalyear.provider", tempFile);

 //      Intent emailIntent = new Intent(Intent.ACTION_SEND);
 //      emailIntent.setType("text/plain");
 //      emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
 //      emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Skin Routine Information");
 //      emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);
 //      emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
 //      emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

 //      if (emailIntent.resolveActivity(getPackageManager()) != null) {
 //          startActivity(emailIntent);
 //      } else {
 //          Toast.makeText(RoutineActivity.this, "No email client found", Toast.LENGTH_SHORT).show();
 //      }
 //  }



 //  private String convertProductsToEmailContent(List<Product> products) {
 //      StringBuilder emailContentBuilder = new StringBuilder();
 //      for (Product product : products) {
 //          emailContentBuilder.append("Product Name: ").append(product.getName()).append("\n");
 //          emailContentBuilder.append("Product Price: ").append(product.getPrice()).append("\n\n");
 //      }
 //      return emailContentBuilder.toString();
 //  }

 //  private String getDownloadUrl(UploadTask.TaskSnapshot taskSnapshot) {
 //      // Assuming the download URL is stored in the taskSnapshot during the upload
 //      Uri downloadUri = taskSnapshot.getUploadSessionUri();

 //      if (downloadUri != null) {
 //          return downloadUri.toString();
 //      } else {
 //          // Handle the case where the download URL is not available
 //          return null;
 //      }
 //  }

    private void showEmailDialog() {
        String subject = "Skincare Routine for " + currentUser.getEmail();
        String body = getSkincareRoutineEmailBody();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(shareIntent, "Share with:"));
    }

    private String getSkincareRoutineEmailBody() {
        StringBuilder body = new StringBuilder();

        // Add Cleanser information to the email body
        body.append("Cleanser:\n");
        List<Product> cleanserProducts = cleanserAdapter.getCleanserList();
        addProductInformationToEmailBody(cleanserProducts, body);

        // Add Treatment information to the email body
        body.append("\n\nTreatment:\n");
        List<Product> treatmentProducts = treatmentAdapter.getTreatmentList();
        addProductInformationToEmailBody(treatmentProducts, body);

        // Add Moisturizer information to the email body
        body.append("\n\nMoisturiser:\n");
        List<Product> moistureProducts = moistureAdapter.getMoistureList();
        addProductInformationToEmailBody(moistureProducts, body);

        return body.toString();
    }

    private void addProductInformationToEmailBody(List<Product> products, StringBuilder body) {
        for (Product product : products) {
            body.append("- ").append(product.getName()).append(": $").append(product.getPrice()).append("\n");
        }
    }
}

