package it.unisa.runnerapp.adapters;

import android.content.Context;
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
import android.widget.Toast;

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
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 02/02/2018.
 */


public class AdActiveAdapter extends ArrayAdapter<ActiveRun> {
    private LayoutInflater inflater;
    AdActiveAdapter.Communicator communicator;
    private List<AdActiveAdapter.ViewHolder> lstHolders;
    private Handler mHandler = new Handler();

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (AdActiveAdapter.ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public AdActiveAdapter(@NonNull Context context, int resource, List<ActiveRun> runs) {
        super(context, resource, runs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstHolders = new ArrayList<AdActiveAdapter.ViewHolder>();
        startUpdateTimer();
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        ActiveRun activeruncurrent = getItem(position);
        AdActiveAdapter.ViewHolder holder = null;

        if (convertView == null) {
            holder = new AdActiveAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.row_adactive, parent, false);
            holder.starthour = (TextView) convertView.findViewById(R.id.starthour);
            holder.datestart = (TextView) convertView.findViewById(R.id.datestart);
            holder.timertw = (TextView) convertView.findViewById(R.id.timer);
            holder.participationbtn = (Button) convertView.findViewById(R.id.participationbtn);
            holder.delayparticipation = (Button) convertView.findViewById(R.id.delayparticipation_btn);
            holder.mapview = (MapView) convertView.findViewById(R.id.pointmeetmap);
            convertView.setTag(holder);
            synchronized (lstHolders) {
                lstHolders.add(holder);
            }
        }
        else {
            holder = (AdActiveAdapter.ViewHolder) convertView.getTag();
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


    public View.OnClickListener getRequestParicipation(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = Integer.parseInt(v.getTag().toString());

                Toast.makeText(getContext(),"CIAO",Toast.LENGTH_LONG).show();
            /*    ActiveRun activeruncurren = (ActiveRun) getItem(tag);
                Button participation = v.findViewById(R.id.participatebtn);
                View view = adsactivefragment.arrayadapter.getView(tag,(View) v.getParent(),null);
                Button cancelrun = (Button) view.findViewById(R.id.cancelbtn);
                cancelrun.setVisibility(View.VISIBLE);
                participation.setVisibility(View.GONE);
                new PActiveRunDaoImpl().createParticipationRun(activeruncurren.getId(),"paolo2796"); */
            }
        };
    }


    private class ViewHolder implements OnMapReadyCallback {

        TextView datestart;
        TextView starthour;
        TextView timertw;
        Button participationbtn;
        Button delayparticipation;
        ActiveRun activerun;
        MapView mapview;
        LatLng pointmeeting;
        GoogleMap googlemap;
        int position;

        public void setData(ActiveRun item, int position) {

            activerun = item;
            this.position = position;
            participationbtn.setTag(position);
            delayparticipation.setTag(position);
            participationbtn.setOnClickListener(getRequestParicipation());
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
                delayparticipation.setVisibility(View.GONE);
                participationbtn.setVisibility(View.VISIBLE);
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                timertw.setText(CheckUtils.parseHourOrMinutes(hours) + ":" + CheckUtils.parseHourOrMinutes(minutes) + ":" + CheckUtils.parseHourOrMinutes(seconds));
            } else {
                timertw.setText("Tempo Scaduto!");
                participationbtn.setVisibility(View.GONE);
                delayparticipation.setVisibility(View.VISIBLE);
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


                Marker marker = googlemap.addMarker(new MarkerOptions()
                        .position(pointmeet)
                        .title("Title")
                        .snippet("Snippet"));

                marker.showInfoWindow();


                googlemap.setInfoWindowAdapter(new AdActiveAdapter.MyInfoWindowAdapter());
                googlemap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        communicator.respondDetailRun(position);
                    }
                });
            }
        }
    } // End Class View Holder

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




    public void setCommunicator(AdActiveAdapter.Communicator communicator) {
        this.communicator = communicator;
    }

    public interface Communicator{

        public void respondDetailRun(int index);
    }

}
