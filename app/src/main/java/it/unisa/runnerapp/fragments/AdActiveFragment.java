package it.unisa.runnerapp.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import it.unisa.runnerapp.beans.ActiveRun;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 01/02/2018.
 */

public class AdActiveFragment extends AdFragment {

    private Button participation;
    private Button cancelrun;
    private Button gorun;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.adactive_fragment, container, false);
        activerun = (ActiveRun) getArguments().getSerializable("activerun");
        starthour = (TextView) v.findViewById(R.id.starthour);
        datestart = (TextView) v.findViewById(R.id.datestart);
        timer = (TextView) v.findViewById(R.id.timer);
        participation = (Button) v.findViewById(R.id.participatebtn);


        CounterClass timer = new CounterClass(activerun.getStartDate().getTime() - System.currentTimeMillis(),1000);
        timer.start();


        starthour.setText(AdFragment.convertHMToStringFormat(activerun.getStartDate()));
        datestart.setText(AdFragment.convertDateToStringFormat(activerun.getStartDate()));



        // Gets the MapView from the XML layout and creates it
        mapview = (MapView) v.findViewById(R.id.pointmeetmap);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        return v;


    }
}
