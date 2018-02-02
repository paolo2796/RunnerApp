package it.unisa.runnerapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unisa.runnerapp.beans.ActiveRun;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 31/01/2018.
 */

public class AdFragment extends Fragment implements AdapterView.OnItemClickListener, OnMapReadyCallback {


    ActiveRun activerun;
    TextView starthour;
    TextView datestart;
    MapView mapview;
    TextView timer;


    public AdFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ad_fragment, container, false);
        activerun = (ActiveRun) getArguments().getSerializable("activerun");
        starthour = (TextView) v.findViewById(R.id.starthour);
        datestart = (TextView) v.findViewById(R.id.datestart);
        timer = (TextView) v.findViewById(R.id.timer);

        CounterClass timer = new CounterClass(activerun.getStartDate().getTime() - System.currentTimeMillis(),1000);
        timer.start();


        starthour.setText(convertHMToStringFormat(activerun.getStartDate()));
        datestart.setText(convertDateToStringFormat(activerun.getStartDate()));



        // Gets the MapView from the XML layout and creates it
        mapview = (MapView) v.findViewById(R.id.pointmeetmap);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        return v;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng pointmeet = new LatLng(activerun.getMeetingPoint().latitude,activerun.getMeetingPoint().longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pointmeet)
                .zoom(20)                   // Imposta lo zoom
                .bearing(90)                // Imposta l'orientamento della camera verso est
                .tilt(30)                   // Rende l'inclinazione della fotocamera a 30°
                .build();                   // Crea una CameraPosition dal Builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.addMarker(new MarkerOptions()
                .position(pointmeet)
                .title("Punto Incontro"));

    }

    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }



    public static String convertDateToStringFormat(Date data){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        String month =  capitalizeFirstLetter(new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)]);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.valueOf( day + " " + month);
    }



    public static String convertHMToStringFormat(Date data){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        return String.valueOf( hours + " : " + minutes);
    }



    public static String capitalizeFirstLetter(String mystring){

        return  mystring.substring(0,1).toUpperCase() + mystring.substring(1);
    }


    public class CounterClass extends CountDownTimer {

        public CounterClass(long milliinfuture, long countdowninterval){

            super(milliinfuture,countdowninterval);
        }

        @Override
        public void onTick(long milliuntilfinished) {

            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliuntilfinished),
                    TimeUnit.MILLISECONDS.toMinutes(milliuntilfinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliuntilfinished)),
                    TimeUnit.MILLISECONDS.toSeconds(milliuntilfinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliuntilfinished)));



            Log.i("Messaggio",hms);

            timer.setText(hms);


        }

        @Override
        public void onFinish() {


            Toast.makeText(getActivity(),"AVVIATA*",Toast.LENGTH_LONG).show();

        }


    }
}
