package com.example.myas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class MobilityExerciseListFragment  extends ListFragment implements AdapterView.OnItemClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mobility_exercise, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.mobility_exercises, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        if(position == 4) {
            startActivity(new Intent(MobilityExerciseListFragment.this.getActivity(), SensorPlacementTrunkRotation.class));
        }else if(position == 5) {
            startActivity(new Intent(MobilityExerciseListFragment.this.getActivity(), SensorPlacementTrunkBend.class));
        } else {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new UnderConstructionFragment()).commit();
        }

    }
}
