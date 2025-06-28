package com.example.glucotrack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GlucoseEntryActivity extends AppCompatActivity {

    private EditText mealInput, insulinInput;
    private EditText glucoseInput;
    private Button saveButton;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );
        setContentView(R.layout.activity_glucose_entry);

        glucoseInput = findViewById(R.id.glucoseInput);
        mealInput = findViewById(R.id.mealInput);
        insulinInput = findViewById(R.id.insulinInput);
        saveButton = findViewById(R.id.saveButton);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        saveButton.setOnClickListener(v -> {
            String glucoseStr = glucoseInput.getText().toString();
            String mealText = mealInput.getText().toString();
            String insulinStr = insulinInput.getText().toString();

            if (glucoseStr.isEmpty()) {
                Toast.makeText(this, "Enter glucose value", Toast.LENGTH_SHORT).show();
                return;
            }

            int glucoseValue = Integer.parseInt(glucoseStr);
            int insulinDose = insulinStr.isEmpty() ? 0 : Integer.parseInt(insulinStr);

            Map<String, Object> reading = new HashMap<>();
            reading.put("glucose", glucoseValue);
            reading.put("meal", mealText);
            reading.put("insulin", insulinDose);
            reading.put("timestamp", Timestamp.now());

            db.collection("users").document(userId)
                    .collection("glucose_readings")
                    .add(reading)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Reading saved!", Toast.LENGTH_SHORT).show();
                        glucoseInput.setText("");
                        mealInput.setText("");
                        insulinInput.setText("");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
