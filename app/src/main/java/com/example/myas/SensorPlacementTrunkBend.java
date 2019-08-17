package com.example.myas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SensorPlacementTrunkBend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_placement);

        final Button button = findViewById(R.id.connectButton);
        button.setOnClickListener( v -> {
            Intent sensorIntent = new Intent(SensorPlacementTrunkBend.this, MetaWearMainActivity.class);
            sensorIntent.putExtra("exercise","trunkbend");
            startActivity(sensorIntent);
        });

    }

}
