package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.List;

import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.FinishedRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 07/02/2018.
 */

public class AdFinishedAdapter extends ArrayAdapter<FinishedRun> {

    private LayoutInflater inflater;
    private AdFinishedAdapter.Communicator communicator;

    public AdFinishedAdapter(@NonNull Context context, int resource, List<FinishedRun> runs) {
        super(context, resource, runs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        FinishedRun runcurrent = getItem(position);

        if (v == null) {
            v = inflater.inflate(R.layout.row_myadsfinished, parent, false);

            TextView starthour = (TextView) v.findViewById(R.id.starthour);
            TextView datestart = (TextView) v.findViewById(R.id.datestart);
            TextView burnedkl = (TextView) v.findViewById(R.id.burnedkal);
            TextView traveledkm = (TextView) v.findViewById(R.id.traveledkm);
            Button detailrunbtn = (Button) v.findViewById(R.id.detailrun_btnfinished);


            //Set values
            starthour.setText(CheckUtils.convertHMToStringFormat(runcurrent.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(runcurrent.getStartDate()));
            DecimalFormat formatter=new DecimalFormat("##.##");
            burnedkl.setText(formatter.format(runcurrent.getBurnedCal()));
            traveledkm.setText(formatter.format(runcurrent.getTraveledKm()));
            detailrunbtn.setTag(position);

            //Set Listeners
            detailrunbtn.setOnClickListener(getDetailRunListener());
        }

        return v;
    }



        public View.OnClickListener getDetailRunListener(){

            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = Integer.parseInt(v.getTag().toString());
                    communicator.respondDetailRun(tag);

                }
            };


        }

    public void setCommunicator(AdFinishedAdapter.Communicator communicator) {this.communicator = communicator;}


    public interface Communicator{

            public void respondDetailRun(int index);
        }


}
