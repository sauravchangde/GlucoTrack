package com.example.glucotrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private Button logoutButton;
    private Button addReadingButton;
    private Button refreshButton;
    private TextView welcomeText;
    private LineChart glucoseChart;
    private TextView glucoseReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        setContentView(R.layout.activity_home);

        logoutButton = findViewById(R.id.logoutButton);
        addReadingButton = findViewById(R.id.addReadingButton);
        refreshButton = findViewById(R.id.refreshButton);
        welcomeText = findViewById(R.id.welcomeText);
        glucoseChart = findViewById(R.id.glucoseChart);
        glucoseReading = findViewById(R.id.glucoseReading);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            welcomeText.setText("Welcome, " + user.getEmail());
            loadGlucoseData(user.getUid());
        }

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        addReadingButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddReadingActivity.class);
            startActivity(intent);
        });

        refreshButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                loadGlucoseData(currentUser.getUid());
                Toast.makeText(this, "Readings refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        scheduleDailyReminder();
    }

    private void loadGlucoseData(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(uid)
                .collection("readings")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Entry> entries = new ArrayList<>();
                    int index = 0;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Double glucoseValue = doc.getDouble("glucose");
                        if (glucoseValue != null) {
                            entries.add(new Entry(index++, glucoseValue.floatValue()));
                        }
                    }

                    if (!entries.isEmpty()) {
                        float latestValue = entries.get(entries.size() - 1).getY();
                        glucoseReading.setText("Latest Glucose: " + latestValue + " mg/dL");

                        LineDataSet dataSet = new LineDataSet(entries, "Glucose Readings");
                        dataSet.setColor(Color.MAGENTA);
                        dataSet.setValueTextColor(Color.WHITE);

                        glucoseChart.setData(new LineData(dataSet));
                        glucoseChart.invalidate(); // Refresh chart
                    } else {
                        glucoseReading.setText("No readings found.");
                        glucoseChart.clear();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching readings", e);
                    Toast.makeText(this, "Failed to load readings", Toast.LENGTH_SHORT).show();
                });
    }

    private void scheduleDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }
}
