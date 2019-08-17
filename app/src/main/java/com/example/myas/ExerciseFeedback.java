package com.example.myas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;
import android.widget.Toast;

public class ExerciseFeedback extends AppCompatActivity {

    public ExerciseFeedback(Context context) {

    }

    private int trunkrotation_zaxis = 0;
    private int trunkrotation_yaxis = 0;
    private int trunkbend_right_forward = 0;
    private int trunkbend_right_backward = 0;
    private int trunkbend_left_wrong = 0;
    private int bridge_arch = 0;
    private int bridge_rightdip = 0;
    private int bridge_leftdip = 0;
    private int totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void streamCountTotal(Integer count) {
        totalCount = count;
    }

    public void tiltForwardFeedback(Integer count) {
        trunkbend_right_forward = count;
        System.out.println("tilt forward " + count);
    }

    public void tiltBackwardFeedback(Integer count) {
        trunkbend_right_backward = count;
        System.out.println("tilt backward " + count);
    }

    public void wrongMovementFeedback(Integer count) {
        trunkbend_left_wrong = count;
        System.out.println("wrong movement " + count);

    }

    public void yaxisWrong(Integer count) {
        trunkrotation_yaxis = count;
        System.out.println("yaxis " + count);
    }

    public void zaxisWrong(Integer count) {
        trunkrotation_zaxis = count;
        System.out.println("zaxis " + count);
    }

    public void backarched(Integer count) {
        bridge_arch = count;
        System.out.println("back arched " + count);

    }

    public void righthipdip(Integer count) {
        bridge_rightdip = count;
        System.out.println("right hip dip " + count);
    }

    public void lefthipdip(Integer count) {
        bridge_leftdip = count;
        System.out.println("left hip dip " + count);
    }

    public void printTotals() {
        System.out.println("total " + totalCount);
    }


    public String getTotal() {
        return Integer.toString(totalCount);
    }

    public String getWrongTotal(int exercise) {
        int wrong = 0;
        if (exercise == 0) {
            wrong = trunkbend_left_wrong + trunkbend_right_backward + trunkbend_right_forward;

        } else if (exercise == 1) {
            wrong = trunkrotation_yaxis + trunkrotation_zaxis;
        } else if (exercise == 2) {
            wrong = bridge_arch + bridge_leftdip + bridge_rightdip;
        }

        return Integer.toString(wrong);

    }

}
