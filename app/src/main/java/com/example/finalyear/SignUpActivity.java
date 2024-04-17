package com.example.finalyear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    // Initialize Firebase Authentication and Firestore
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private Button signUpButton;
    private EditText username, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        signUpButton = findViewById(R.id.button);
        username = findViewById(R.id.username); // Initialize the username EditText
        email = findViewById(R.id.email);
        password = findViewById(R.id.password1);
        confirmPassword = findViewById(R.id.password2);

        // Add an OnClickListener to your Sign Up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username1 = username.getText().toString(); // Get the username entered by the user
                final String email1 = email.getText().toString();
                final String password1 = password.getText().toString();
                String confirmPassword1 = confirmPassword.getText().toString();

                if (password1.equals(confirmPassword1)) {
                    mAuth.createUserWithEmailAndPassword(email1, password1)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User registered successfully
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        // Save the user's email and username into Firestore
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", email1);
                                        userMap.put("username", username1);

                                        // Store the password temporarily (not recommended)
                                        userMap.put("password", password1);

                                        mFirestore.collection("Users").document(user.getUid())
                                                .set(userMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // The user's email, username, and password were saved successfully into Firestore
                                                        Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // The user's email, username, and password were not saved into Firestore
                                                        Toast.makeText(SignUpActivity.this, "Failed to save user's information into Firestore.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // Registration failed
                                        Toast.makeText(SignUpActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
