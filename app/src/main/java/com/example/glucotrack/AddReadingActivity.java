package com.example.glucotrack;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddReadingActivity extends AppCompatActivity {

    private EditText glucoseInput;
    private Button saveReadingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reading);

        glucoseInput = findViewById(R.id.glucoseInput);
        saveReadingButton = findViewById(R.id.saveReadingButton);

        saveReadingButton.setOnClickListener(v -> {
            String glucoseText = glucoseInput.getText().toString().trim();

            if (!glucoseText.isEmpty()) {
                try {
                    float glucoseValue = Float.parseFloat(glucoseText);
                    saveReading(glucoseValue);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveReading(float value) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> reading = new HashMap<>();
        reading.put("glucose", value);
        reading.put("timestamp", Timestamp.now());

        db.collection("users")
                .document(uid)
                .collection("readings")
                .add(reading)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reading saved", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to HomeActivity
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving reading", e);
                    Toast.makeText(this, "Error saving reading", Toast.LENGTH_SHORT).show();
                });
    }
}
