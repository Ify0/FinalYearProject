package com.example.finalyear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

public class ImageScanner extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final String TAG = "ImageScanner";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;

    private LinearLayout loadingLayout;
    private FirebaseFirestore firestore;
    private CollectionReference uploadsRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private AlertDialog loadingDialog;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

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
        MaterialButton nextButton = findViewById(R.id.imageButton4);
        Button useCameraButton = findViewById(R.id.cameraBtn);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("uploads");


        uploadButton.setOnClickListener(view -> openFileChooser());

        useCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermissionAndOpenCamera();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        loadingLayout = findViewById(R.id.loadingLayout);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform image analysis and save result to the database
                showLoadingLayout();
                analyzeAndSaveToDatabase();
            }
        });
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            pickImageCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImageCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        // Handle the captured image URI as needed (e.g., display in an ImageView)
                        imageView.setImageURI(imageUri);
                    } else {
                        Toast.makeText(ImageScanner.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        // Set up preview
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        // Set up image capture
        imageCapture = new ImageCapture.Builder().build();

        // Unbind use cases before rebinding
        cameraProvider.unbindAll();

        // Bind use cases to camera
        Camera camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
        );


    }

    private void showLoadingLayout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);

        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingLayout() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
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
                // Get the screen dimensions
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                // Calculate maximum dimensions based on screen size
                int maxWidth = (int) (screenWidth * 0.8); // 80% of screen width
                int maxHeight = (int) (screenHeight * 0.8); // 80% of screen height

                // Load the image into the image view with the calculated dimensions
                Picasso.get().load(imageUri).resize(maxWidth, maxHeight).centerInside().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ImageAnalysisTask extends AsyncTask<Void, Void, String> {

        private final Uri uri;
        private final Timestamp timestamp;

        public ImageAnalysisTask(Uri uri , Timestamp timestamp) {
            this.uri = uri;
            this.timestamp = timestamp;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Convert Uri to URL
            String imageUrl = uri.toString();

            // Create an instance of ImageAnalyzer
            ImageAnalyzer imageAnalyzer = new ImageAnalyzer();

            // Call analyzeImage with the provided callback
            imageAnalyzer.analyzeImage(imageUrl, new ImageAnalyzer.AnalysisCallback() {
                @Override
                public void onAnalysisSuccess(String imageUrl, String result) {
                    // Save the result to the database
                    saveToFirebase(imageUrl, result, timestamp)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(TAG, "Upload successful");
                                // Start next activity after result has been saved to Firestore
                                Intent intent = new Intent(ImageScanner.this, ResultsActivity.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Upload failed: " + e.getMessage());
                            });
                }

                @Override
                public void onAnalysisFailure(String imageUrl, String errorMessage) {
                    // Save the failure message to the database
                    saveToFirebase(imageUrl, errorMessage, timestamp)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(TAG, "Upload successful");

                                Intent intent = new Intent(ImageScanner.this, ResultsActivity.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Upload failed: " + e.getMessage());
                            });
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // This method will be called after doInBackground completes
            hideLoadingLayout();
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
                            Timestamp timestamp = Timestamp.now();
                            new ImageAnalysisTask(uri, timestamp).execute();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ImageScanner.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private Task<DocumentReference> saveToFirebase(String imageUrl, String result, Timestamp timestamp) {
        if (uploadsRef != null) {
            Upload upload = new Upload(imageUrl, result, timestamp);
            return uploadsRef.add(upload);
        } else {
            Log.e(TAG, "User not authenticated");
            return null;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
