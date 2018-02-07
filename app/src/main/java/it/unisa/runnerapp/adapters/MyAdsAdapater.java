package it.unisa.runnerapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
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

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.fragments.MyAdsFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 07/02/2018.
 */

public class MyAdsAdapater extends ArrayAdapter<ActiveRun> {

    private LayoutInflater inflater;
    Communicator communicator;

    public MyAdsAdapater(@NonNull Context context, int resource, List<ActiveRun> runs) {
        super(context, resource, runs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        MyAdsAdapater.ViewHolder holder = new MyAdsAdapater.ViewHolder();
        ActiveRun activeruncurrent = getItem(position);

        if (v == null) {

            v = inflater.inflate(R.layout.row_myads, parent, false);
            holder.initializeMap(activeruncurrent.getMeetingPoint(), v, position);
            TextView starthour = (TextView) v.findViewById(R.id.starthour);
            TextView datestart = (TextView) v.findViewById(R.id.datestart);
            Button delayparticipationbtn = (Button) v.findViewById(R.id.delayparticipation_btn);
            TextView timertw = (TextView) v.findViewById(R.id.timer);
            final Button deleterunbtn = (Button) v.findViewById(R.id.deleterun_btn);
            final Button editrunbtn = (Button) v.findViewById(R.id.editrun_btn);
            editrunbtn.setTag(position);
            deleterunbtn.setTag(position);
            deleterunbtn.setOnTouchListener(getOnTouchListenerDelete(editrunbtn));
            editrunbtn.setOnTouchListener(getOnTouchListnerEdit(deleterunbtn));
            starthour.setText(CheckUtils.convertHMToStringFormat(activeruncurrent.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(activeruncurrent.getStartDate()));
            timertw.setText(String.valueOf(position));

            MyAdsAdapater.CounterClass timer = new MyAdsAdapater.CounterClass(activeruncurrent.getStartDate().getTime() - System.currentTimeMillis(), 1000, timertw, deleterunbtn, editrunbtn, delayparticipationbtn);
            timer.start();


        }

        return v;
    }


    public View.OnTouchListener getOnTouchListenerDelete(final Button editrunbtn){
       return  (new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {

               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 10.0f));
                   editrunbtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
               }
               else if(event.getAction() == MotionEvent.ACTION_UP){
                   int tag = Integer.valueOf((v.getTag().toString()));
                   ActiveRun runtag = getItem(tag);
                   communicator.respondConfirmDelete(runtag);

               }
               return true;
           }
        });

    }


    public View.OnTouchListener getOnTouchListnerEdit(final Button deleterunbtn){
        return  (new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    deleterunbtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                    v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 10.0f));
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){




                }
                return true;
            }
        });

    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }


     class CounterClass extends CountDownTimer {
        TextView timertw;
        Button deleterun;
        Button delayparticipation;
        Button editrunbtn;

        public CounterClass(long millisInFuture, long countDownInterval, TextView timertw, Button deleterun, Button editrunbtn, Button delayparticipation) {
            super(millisInFuture, countDownInterval);
            this.timertw = timertw;
            this.deleterun = deleterun;
            this.editrunbtn = editrunbtn;
            this.delayparticipation = delayparticipation;
        }

        @Override
        public void onFinish() {
            timertw.setText(getContext().getResources().getText(R.string.timer_delay));
            deleterun.setVisibility(View.GONE);
            editrunbtn.setVisibility(View.GONE);
            delayparticipation.setVisibility(View.VISIBLE);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            synchronized (this) {
                timertw.setText(hms);

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


                googlemap.setInfoWindowAdapter(new MyAdsAdapater.MyInfoWindowAdapter());
                googlemap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        communicator.respond(position);
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


    public interface Communicator{

        public void respond(int position);
        public void respondEdit(int position);
        public void respondConfirmDelete(ActiveRun runtag);
    }
}
