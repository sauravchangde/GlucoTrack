package com.example.glucotrack;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;
import com.google.firebase.Timestamp;


public class GlucoseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GlucoseAdapter adapter;
    private List<GlucoseReading> readings = new ArrayList<>();
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
        setContentView(R.layout.activity_glucose_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GlucoseAdapter(readings);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("glucose_readings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    readings.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        int glucose = doc.getLong("glucose").intValue();
                        Timestamp timestamp = doc.getTimestamp("timestamp");
                        readings.add(new GlucoseReading(glucose, timestamp.toDate()));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
