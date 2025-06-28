package com.example.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;


public class SignUpActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button signUpButton, goToLoginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpButton = findViewById(R.id.signUpButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);

        signUpButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        goToLoginButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }
}
