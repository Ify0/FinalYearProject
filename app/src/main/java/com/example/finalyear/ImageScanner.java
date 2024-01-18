package com.example.finalyear;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class ImageScanner extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;

    private FirebaseFirestore firestore;
    private CollectionReference uploadsRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image_activity);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uploadsRef = firestore.collection("Users").document(user.getUid()).collection("uploads");
        }

        // Initialize views
        imageView = findViewById(R.id.imageView);
        Button uploadButton = findViewById(R.id.uploadButton);
        ImageButton nextButton = findViewById(R.id.imageButton4);


        // Inside onCreate
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("uploads"); // "uploads" is the name of your storage folder


        // Set click listeners
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform image analysis and save result to the database
                analyzeAndSaveToDatabase();
                Intent intent = new Intent(ImageScanner.this, ResultsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Picasso.get().load(imageUri).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ImageAnalysisTask extends AsyncTask<Void, Void, String> {

        private final Uri uri;

        public ImageAnalysisTask(Uri uri) {
            this.uri = uri;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Convert Uri to URL
            //URL url = new URL(uri.toString());
            String imageUrl = uri.toString();

            // Create an instance of ImageAnalyzer
            ImageAnalyzer imageAnalyzer = new ImageAnalyzer();

            // Call analyzeImage with the provided callback
            imageAnalyzer.analyzeImage(imageUrl, new ImageAnalyzer.AnalysisCallback() {
                @Override
                public void onAnalysisSuccess(String imageUrl, String result) {
                    // Handle the success result if needed
                    // Now, you can save the result to the database
                    saveToFirebase(imageUrl, result);
                }

                @Override
                public void onAnalysisFailure(String imageUrl, String errorMessage) {
                    // Handle the failure result if needed
                    // Now, you can save the failure message to the database
                    saveToFirebase(imageUrl, errorMessage);
                }
            });

            // You might want to return some value or null based on your needs
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // This method will be called after doInBackground completes
            // Now, you have already saved the result to the database in onAnalysisSuccess/onAnalysisFailure
        }
    }

    private void analyzeAndSaveToDatabase() {
        if (imageUri != null) {
            // Generate a unique filename for the image
            String fileExtension = getFileExtension(imageUri);
            String fileName = System.currentTimeMillis() + "." + fileExtension;

            // Create a reference to the image in Firebase Storage
            StorageReference imageRef = storageReference.child(fileName);

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Perform image analysis and save result to the database
                            new ImageAnalysisTask(uri).execute();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ImageScanner.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveToFirebase(String imageUrl, String result) {
        if (uploadsRef != null) {
            Upload upload = new Upload(imageUrl, result);
            uploadsRef.add(upload)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ImageScanner.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ImageScanner.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
