package com.example.glucotrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.*;

public class GlucoseAdapter extends RecyclerView.Adapter<GlucoseAdapter.ViewHolder> {

    private final List<GlucoseReading> data;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

    public GlucoseAdapter(List<GlucoseReading> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView glucoseText, timestampText;

        public ViewHolder(View v) {
            super(v);
            glucoseText = v.findViewById(R.id.glucoseText);
            timestampText = v.findViewById(R.id.timestampText);
        }
    }

    @Override
    public GlucoseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_glucose, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GlucoseReading reading = data.get(position);
        holder.glucoseText.setText("Glucose: " + reading.glucose + " mg/dL");
        holder.timestampText.setText("Time: " + sdf.format(reading.timestamp));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
