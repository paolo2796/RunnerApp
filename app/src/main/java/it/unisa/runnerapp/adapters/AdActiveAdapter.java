package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Layout;
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
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.Dao.Interf.PActiveRunDao;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.ConnectionUtil;
import testapp.com.runnerapp.AdsActivity;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 02/02/2018.
 */

public class AdActiveAdapter extends ArrayAdapter<ActiveRun> {
    private PActiveRunDao pactiverundao;
    private ActiveRunDao activerundao;
    private LayoutInflater inflater;
    private List<ActiveRun> runsbyrun;


    public AdActiveAdapter(@NonNull Context context, int resource, List<ActiveRun> runs) {
        super(context, resource,runs);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        pactiverundao = new PActiveRunDaoImpl();
        activerundao = new ActiveRunDaoImpl();

        runsbyrun = pactiverundao.findRunByRunner("paolo2796");
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        AdActiveAdapter.ViewHolder holder = new AdActiveAdapter.ViewHolder();
        ActiveRun activeruncurrent =  getItem(position);

        if (v == null) {

            v = inflater.inflate(R.layout.row_adactive, parent, false);

            holder.initializeMap(activeruncurrent.getMeetingPoint(), v);

            TextView starthour = (TextView) v.findViewById(R.id.starthour);
            TextView datestart = (TextView) v.findViewById(R.id.datestart);
            Button delayparticipationbtn = (Button) v.findViewById(R.id.delayparticipation_btn);
            TextView timertw = (TextView) v.findViewById(R.id.timer);
            Button participationbtn = (Button) v.findViewById(R.id.participatebtn);
            Button cancelrunbtn = (Button) v.findViewById(R.id.cancelbtn);
            cancelrunbtn.setTag(position);
            participationbtn.setTag(position);

            starthour.setText(CheckUtils.convertHMToStringFormat(activeruncurrent.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(activeruncurrent.getStartDate()));
            timertw.setText(String.valueOf(position));

            CounterClass timer = new CounterClass(activeruncurrent.getStartDate().getTime() - System.currentTimeMillis(),1000,timertw,participationbtn,cancelrunbtn,delayparticipationbtn);
            timer.start();

            for(Run run: runsbyrun){
                if(run.getId() == activeruncurrent.getId()) {
                    participationbtn.setVisibility(View.GONE);
                    cancelrunbtn.setVisibility(View.VISIBLE);
                }
            }


        }

        return v;
    }

    public class CounterClass extends CountDownTimer {
        TextView timertw;
        Button participation;
        Button delayparticipation;
        Button cancelrun;
        public CounterClass(long millisInFuture, long countDownInterval, TextView timertw,Button participation, Button cancelrun,Button delayparticipation) {
            super(millisInFuture, countDownInterval);
            this.timertw = timertw;
            this.participation=participation;
            this.cancelrun=cancelrun;
            this.delayparticipation=delayparticipation;
        }
        @Override
        public void onFinish() {
            timertw.setText(getContext().getResources().getText(R.string.timer_delay));
            participation.setVisibility(View.GONE);
            cancelrun.setVisibility(View.GONE);
            delayparticipation.setVisibility(View.VISIBLE);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    synchronized (this){
                        timertw.setText(hms);;
                    }
        }
    }





    class ViewHolder implements OnMapReadyCallback {

        MapView mapView;
        LatLng pointmeeting;

        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng pointmeet = new LatLng(pointmeeting.latitude,pointmeeting.longitude);
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

        public void initializeMap(LatLng pointmeeting, View convertView){

            this.pointmeeting = pointmeeting;
            mapView = (MapView) convertView.findViewById(R.id.pointmeetmap);
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

    } // end class Holder


}
