package com.example.finalyear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText usernameField, passwordField;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                // Query Firestore to find the user document with the given username
                db.collection("Users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // If a document with the given username exists
                                    if (!task.getResult().isEmpty()) {
                                        // Get the first document (assuming username is unique)
                                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                        String email = (String) document.get("email");

                                        // Sign in using the email retrieved from Firestore and the entered password
                                        auth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success
                                                            FirebaseUser user = auth.getCurrentUser();
                                                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {

                                        Toast.makeText(LoginActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
