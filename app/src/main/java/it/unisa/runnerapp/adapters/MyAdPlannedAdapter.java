package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.MyAdsPlannedFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 08/02/2018.
 */

public class MyAdPlannedAdapter extends ArrayAdapter<ActiveRun> {

    private LayoutInflater inflater;
    Communicator communicator;
    private List<MyAdPlannedAdapter.ViewHolder> lstHolders;
    private Handler mHandler = new Handler();

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (MyAdPlannedAdapter.ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public MyAdPlannedAdapter(@NonNull Context context, int resource, List<ActiveRun> runs) {
        super(context, resource, runs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstHolders = new ArrayList<ViewHolder>();
        startUpdateTimer();

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ActiveRun activeruncurrent = getItem(position);
        MyAdPlannedAdapter.ViewHolder holder = null;

        if (convertView == null) {
            holder = new MyAdPlannedAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.row_myadsplanned, parent, false);
            holder.starthour = (TextView) convertView.findViewById(R.id.starthour);
            holder.datestart = (TextView) convertView.findViewById(R.id.datestart);
            holder.timertw = (TextView) convertView.findViewById(R.id.timer);
            holder.cancelrunbtn = (Button) convertView.findViewById(R.id.cancelbtn);
            holder.startlivebtn = (Button) convertView.findViewById(R.id.startlive_btn);
            holder.mapview = (MapView) convertView.findViewById(R.id.pointmeetmap);
            convertView.setTag(holder);
            synchronized (lstHolders) {
                lstHolders.add(holder);
            }
        }
        else {
            holder = (MyAdPlannedAdapter.ViewHolder) convertView.getTag();
        }
        holder.setData(getItem(position), position);

        return convertView;

    }


    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }






    public View.OnClickListener getCancelParticipation(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = Integer.parseInt(v.getTag().toString());
                ActiveRun activeruncurren = (ActiveRun) getItem(tag);
                new PActiveRunDaoImpl().deleteParticipationRun(activeruncurren.getId(),"paolo2796");
                MyAdPlannedAdapter.this.remove(activeruncurren);
                MyAdPlannedAdapter.this.notifyDataSetChanged();

            }
        };
    }

    public View.OnClickListener getStartLiveListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = Integer.parseInt(v.getTag().toString());
                communicator.respondStartLive(tag);
            }
        };

    }




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


    private class ViewHolder implements OnMapReadyCallback {

        TextView datestart;
        TextView starthour;
        TextView timertw;
        Button cancelrunbtn;
        Button startlivebtn;
        ActiveRun activerun;
        MapView mapview;
        LatLng pointmeeting;
        GoogleMap googlemap;
        int position;

        public void setData(ActiveRun item, int position) {

            activerun = item;
            this.position = position;
            cancelrunbtn.setTag(position);
            startlivebtn.setTag(position);
            cancelrunbtn.setOnClickListener(getCancelParticipation());
            startlivebtn.setOnClickListener(getStartLiveListener());
            starthour.setText(CheckUtils.convertHMToStringFormat(item.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(item.getStartDate()));
            pointmeeting = activerun.getMeetingPoint();
            mapview.onCreate(null);
            mapview.getMapAsync(this);
            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {
            long timeDiff = activerun.getStartDate().getTime() - currentTime;
            if (timeDiff > 0) {
                startlivebtn.setVisibility(View.GONE);
                cancelrunbtn.setVisibility(View.VISIBLE);
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                timertw.setText(CheckUtils.parseHourOrMinutes(hours) + ":" + CheckUtils.parseHourOrMinutes(minutes) + ":" + CheckUtils.parseHourOrMinutes(seconds));
            } else {
                timertw.setText("Tempo Scaduto!");
                cancelrunbtn.setVisibility(View.GONE);
                startlivebtn.setVisibility(View.VISIBLE);
            }
        }

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


                googlemap.setInfoWindowAdapter(new MyAdPlannedAdapter.MyInfoWindowAdapter());
                googlemap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        communicator.respondDetailRun(position);
                    }
                });
            }
        }
    } // End Class View Holder




    public interface Communicator{
        public void respondDetailRun(int position);
        public void respondStartLive(int position);
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }


}
