package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    public AdFinishedAdapter(@NonNull Context context, int resource, List<FinishedRun> runs) {
        super(context, resource, runs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        FinishedRun runcurrent = getItem(position);

        if (v == null) {
            v = inflater.inflate(R.layout.row_myadsfinished, parent, false);
            AdFinishedAdapter.ViewHolder holder = new AdFinishedAdapter.ViewHolder();
            holder.initializeMap(runcurrent.getMeetingPoint(), v, position);

        }


            TextView starthour = (TextView) v.findViewById(R.id.starthour);
            TextView datestart = (TextView) v.findViewById(R.id.datestart);
            TextView burnedkl = (TextView)  v.findViewById(R.id.burnedkal);
            TextView traveledkm = (TextView) v.findViewById(R.id.traveledkm);

            starthour.setText(CheckUtils.convertHMToStringFormat(runcurrent.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(runcurrent.getStartDate()));
            burnedkl.setText(String.valueOf(runcurrent.getBurnedCal()));
            traveledkm.setText(String.valueOf(runcurrent.getTraveledKm()));


        return v;
    }


    class ViewHolder implements OnMapReadyCallback {

        MapView mapView;
        LatLng pointmeeting;
        GoogleMap googlemap;
        int position;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            this.googlemap = googleMap;
            if(googlemap!=null) {
                final LatLng pointmeet = new LatLng(pointmeeting.latitude, pointmeeting.longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(pointmeet)
                        .zoom(20)                   // Imposta lo zoom
                        .bearing(90)                // Imposta l'orientamento della camera verso est
                        .tilt(30)                   // Rende l'inclinazione della fotocamera a 30°
                        .build();                   // Crea una CameraPosition dal Builder
                googlemap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                Marker marker = googlemap.addMarker(new MarkerOptions().position(pointmeet).title("Dove ti sei incontrato"));

                marker.showInfoWindow();


              /*  googlemap.setInfoWindowAdapter(new AdFinishedAdapter.MyInfoWindowAdapter());
                googlemap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        communicator.respond(position);
                    }
                }); PER ORA NON SERVE */

            }
        }



        public void initializeMap(LatLng pointmeeting, View convertView, int position){
            this.position=position;
            this.pointmeeting = pointmeeting;
            mapView = (MapView) convertView.findViewById(R.id.pointmeetmap);
            mapView.onCreate(null);
            mapView.getMapAsync(this);

        }

    } // end class Holder



    /* PER ORA NON SERVE
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){

            myContentsView = inflater.inflate(R.layout.custom_info_direction, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {

            return myContentsView;
        }
    } // end class MyInfoWindowAdapter */



    // Definizione Communicator con AdsFinishedFragment e relativi metodi




}
