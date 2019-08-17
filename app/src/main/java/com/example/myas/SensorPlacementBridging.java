package com.example.myas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class SensorPlacementBridging extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_placement_bridging);

        final Button button = findViewById(R.id.connectButton);
        button.setOnClickListener( v -> {
            Intent sensorIntent = new Intent(SensorPlacementBridging.this, MetaWearMainActivity.class);
            sensorIntent.putExtra("exercise","bridging");
            startActivity(sensorIntent);
        });

    }

}
