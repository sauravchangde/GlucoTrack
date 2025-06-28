package com.example.glucotrack;

import java.util.Date;

public class GlucoseReading {
    public int glucose;
    public Date timestamp;

    public GlucoseReading(int glucose, Date timestamp) {
        this.glucose = glucose;
        this.timestamp = timestamp;
    }
}
