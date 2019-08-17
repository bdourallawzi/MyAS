package com.example.myas;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ExerciseListFragment extends ListFragment implements OnItemClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Exercises, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        //Intent exerciseIntent = new Intent(ExerciseListFragment.this.getActivity(), SensorPlacementTrunkBend.class);
        if (position == 0){
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MobilityExerciseListFragment()).commit();
        }else if (position == 1) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new StrengthExerciseListFragment()).commit();
        }else{
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new UnderConstructionFragment()).commit();
        }

        //startActivity(exerciseIntent);
    }
}
