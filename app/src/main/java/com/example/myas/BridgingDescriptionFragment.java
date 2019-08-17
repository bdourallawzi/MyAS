package com.example.myas;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BridgingDescriptionFragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bridging_descr, container, false);

        //ImageView trunkView = (ImageView) view.findViewById(R.id.trunk_anim);
        //trunkView.setBackgroundResource(R.drawable.trunk_animation);
        //ImageView mImageViewFilling = (ImageView) view.findViewById(R.id.trunk_anim);
        //((AnimationDrawable) trunkView.getBackground()).start();

        final Button button = view.findViewById(R.id.play_video);
        button.setOnClickListener( v -> {
            Intent sensorIntent = new Intent(BridgingDescriptionFragment.this.getActivity(), WorkoutYoutube.class);
            sensorIntent.putExtra("id", "tDoIr1cUxtE");
            startActivity(sensorIntent);
        });

        return view;
    }

    }
