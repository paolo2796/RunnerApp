package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
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
import java.util.concurrent.TimeUnit;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 02/02/2018.
 */


public class AdActiveAdapter extends ArrayAdapter<ActiveRun> {
    private LayoutInflater inflater;
    private List<Run> runsbyrun;
    AdsActiveFragment.Communicator communicator;

    public AdActiveAdapter(@NonNull Context context, int resource, List<ActiveRun> runs) {
        super(context, resource, runs);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        runsbyrun = new PActiveRunDaoImpl().findRunByRunnerFetchID("paolo2796");
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        AdActiveAdapter.ViewHolder holder = new AdActiveAdapter.ViewHolder();
        ActiveRun activeruncurrent = getItem(position);

        if (v == null) {

            v = inflater.inflate(R.layout.row_adactive, parent, false);

            holder.initializeMap(activeruncurrent.getMeetingPoint(), v, position);

            TextView starthour = (TextView) v.findViewById(R.id.starthour);
            TextView datestart = (TextView) v.findViewById(R.id.datestart);
            Button delayparticipationbtn = (Button) v.findViewById(R.id.delayparticipation_btn);
            TextView timertw = (TextView) v.findViewById(R.id.timer);
            Button participationbtn = (Button) v.findViewById(R.id.participatebtn);
            Button cancelrunbtn = (Button) v.findViewById(R.id.cancelbtn);
            Button startlivebtn = (Button) v.findViewById(R.id.startlive_btn);
            cancelrunbtn.setTag(position);
            participationbtn.setTag(position);
            startlivebtn.setTag(position);

            starthour.setText(CheckUtils.convertHMToStringFormat(activeruncurrent.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(activeruncurrent.getStartDate()));
            timertw.setText(String.valueOf(position));

            CounterClass timer = new CounterClass(activeruncurrent.getStartDate().getTime() - System.currentTimeMillis(), 1000, timertw, participationbtn, cancelrunbtn, delayparticipationbtn, startlivebtn,activeruncurrent);
            timer.start();

            for (Run run : runsbyrun) {
                if (run.getId() == activeruncurrent.getId()) {
                    participationbtn.setVisibility(View.GONE);
                    cancelrunbtn.setVisibility(View.VISIBLE);
                }
            }


        }

        return v;
    }



    public void setCommunicator(AdsActiveFragment.Communicator communicator) {
        this.communicator = communicator;
    }


    public class CounterClass extends CountDownTimer {
        TextView timertw;
        Button participation;
        Button delayparticipation;
        Button cancelrun;
        Button startlivebtn;
        ActiveRun activerun;

        public CounterClass(long millisInFuture, long countDownInterval, TextView timertw, Button participation, Button cancelrun, Button delayparticipation,Button startlivebtn, ActiveRun activerun) {
            super(millisInFuture, countDownInterval);
            this.activerun = activerun;
            this.timertw = timertw;
            this.participation = participation;
            this.cancelrun = cancelrun;
            this.delayparticipation = delayparticipation;
            this.startlivebtn = startlivebtn;
        }

        @Override
        public void onFinish() {
            timertw.setText(getContext().getResources().getText(R.string.timer_delay));
            boolean isparticipate =false;
            for(Run runbyrun: runsbyrun) {
                if (runbyrun.getId() == activerun.getId()) {
                    isparticipate = true;
                }
            }

            if(isparticipate){
                Log.i("Message","SONO QUA ISPARTICIPATE");
                delayparticipation.setVisibility(View.GONE);
                startlivebtn.setVisibility(View.VISIBLE);
            }

            else{
                delayparticipation.setVisibility(View.VISIBLE);
                startlivebtn.setVisibility(View.GONE);
            }

            participation.setVisibility(View.GONE);
            cancelrun.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            synchronized (this) {
                timertw.setText(hms);
                ;
            }
        }
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

                // Bitmap bitmapicon =  CheckUtils.getBitmapFromVectorDrawable(AdActiveAdapter.this.getContext(),R.drawable.ic_info_marker_54dp);
                // marker.icon(BitmapDescriptorFactory.fromBitmap(bitmapicon));

                Marker marker = googlemap.addMarker(new MarkerOptions()
                        .position(pointmeet)
                        .title("Title")
                        .snippet("Snippet"));

                marker.showInfoWindow();


                googlemap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                googlemap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        communicator.respondAdsActive(position);
                    }
                });

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
    } // end class MyInfoWindowAdapter

}
