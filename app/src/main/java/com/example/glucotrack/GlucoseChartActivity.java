package com.example.glucotrack;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.Timestamp;


import java.text.SimpleDateFormat;
import java.util.*;

public class GlucoseChartActivity extends AppCompatActivity {

    private LineChart chart;
    private FirebaseFirestore db;
    private String userId;
    private List<Entry> entries = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );
        setContentView(R.layout.activity_glucose_chart);

        chart = findViewById(R.id.lineChart);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("glucose_readings")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    int index = 0;
                    for (QueryDocumentSnapshot doc : snapshot) {
                        int glucose = doc.getLong("glucose").intValue();
                        Timestamp ts = doc.getTimestamp("timestamp");
                        entries.add(new Entry(index, glucose));
                        labels.add(dateFormat.format(ts.toDate()));
                        index++;
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Glucose (mg/dL)");
                    dataSet.setColor(Color.BLUE);
                    dataSet.setValueTextColor(Color.BLACK);
                    dataSet.setLineWidth(2f);
                    dataSet.setCircleRadius(4f);
                    dataSet.setDrawValues(false);

                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setGranularity(1f);
                    xAxis.setLabelRotationAngle(-45);

                    chart.getDescription().setText("Glucose Trends");
                    chart.animateX(1000);
                    chart.invalidate();
                });
    }
}
