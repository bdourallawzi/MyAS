package com.example.myas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class Pop extends AppCompatActivity {

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.count_popup);
        String name= getIntent().getStringExtra("name");
        String totalCount= getIntent().getStringExtra("totalcount");
        String wrongCount= getIntent().getStringExtra("wrongcount");

        TextView nameText = findViewById(R.id.exercise_name);
        TextView totalText = findViewById(R.id.total_count);
        TextView wrongText = findViewById(R.id.wrongmove1);

        nameText.setText(String.format("%s%s", getString(R.string.exercisename), name));
        totalText.setText(String.format("%s%s", getString(R.string.totalcountpop), totalCount));
        wrongText.setText(String.format("%s%s", getString(R.string.wrongcountpop), wrongCount));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width* 0.8), (int) (height * 0.6));
    }
}
