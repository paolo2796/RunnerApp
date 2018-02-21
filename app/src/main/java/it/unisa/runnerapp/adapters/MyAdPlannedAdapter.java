package it.unisa.runnerapp.adapters;

import android.content.Context;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.MyAdsPlannedFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.MainActivityPV;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 08/02/2018.
 */

public class MyAdPlannedAdapter extends ArrayAdapter<ActiveRun> {

    private LayoutInflater inflater;
    Communicator communicator;
    private List<MyAdPlannedAdapter.ViewHolder> lstHolders;
    private Handler mHandler = new Handler();
    HashMap<Integer,Integer> maprunpos;


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
        maprunpos = new HashMap<>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ActiveRun activeruncurrent = getItem(position);
        MyAdPlannedAdapter.ViewHolder holder = null;

        if (convertView == null) {

            maprunpos.put(activeruncurrent.getId(),position);
            holder = new MyAdPlannedAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.row_myadsplanned, parent, false);
            holder.starthour = (TextView) convertView.findViewById(R.id.starthour);
            holder.datestart = (TextView) convertView.findViewById(R.id.datestart);
            holder.timertw = (TextView) convertView.findViewById(R.id.timer);
            holder.cancelrunbtn = (Button) convertView.findViewById(R.id.cancelbtn);
            holder.startlivebtn = (Button) convertView.findViewById(R.id.startlive_btn);
            holder.estimatedkmtw = (TextView) convertView.findViewById(R.id.estimatedkm_tw);
            holder.estimatedtimetw = (TextView) convertView.findViewById(R.id.estimatedtime_tw);
            holder.pointmeetingimg = (ImageButton) convertView.findViewById(R.id.gowaypoint_btn);

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


                if(activeruncurren.getMaster().getNickname().equals(MainActivityPV.userlogged.getNickname())){

                    Toast.makeText(MyAdPlannedAdapter.this.getContext(), "Sei il proprietario di questo annuncio. Devi partecipare in quanto sei master!", Toast.LENGTH_SHORT).show();


                }
                else {
                    new PActiveRunDaoImpl().deleteParticipationRun(activeruncurren.getId(), MainActivityPV.userlogged.getNickname());
                    MyAdsPlannedFragment.removeParticipationFirebase(activeruncurren, MainActivityPV.userlogged.getNickname());
                    MyAdPlannedAdapter.this.remove(activeruncurren);
                    MyAdPlannedAdapter.this.notifyDataSetChanged();
                }

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

    public View.OnClickListener getClicklPointMeetingListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = Integer.parseInt(v.getTag().toString());
                communicator.respondDetailRun(tag);
            }
        };

    }


    private class ViewHolder{

        TextView datestart;
        TextView starthour;
        TextView timertw;
        Button cancelrunbtn;
        Button startlivebtn;
        ActiveRun activerun;
        LatLng pointmeeting;
        TextView estimatedkmtw;
        TextView estimatedtimetw;
        ImageButton pointmeetingimg;
        int position;

        public void setData(ActiveRun item, int position) {

            this.activerun = item;
            this.position = position;
            cancelrunbtn.setTag(position);
            startlivebtn.setTag(position);
            pointmeetingimg.setTag(position);
            cancelrunbtn.setOnClickListener(getCancelParticipation());
            startlivebtn.setOnClickListener(getStartLiveListener());
            starthour.setText(CheckUtils.convertHMToStringFormat(item.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(item.getStartDate()));
            pointmeeting = activerun.getMeetingPoint();
            estimatedtimetw.setText(String.valueOf(item.getEstimatedHours() + " h " + item.getEstimatedMinutes() + "m"));
            estimatedkmtw.setText(String.valueOf(item.getEstimatedKm()) + "km");
            pointmeetingimg.setOnClickListener(getClicklPointMeetingListener());

            Animation animation = AnimationUtils.loadAnimation(MyAdPlannedAdapter.this.getContext(),R.anim.scaling);
            pointmeetingimg.startAnimation(animation);
            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {
            long timeDiff = activerun.getStartDate().getTime() - currentTime;
            if (timeDiff > 0) {
                startlivebtn.setVisibility(View.GONE);
                cancelrunbtn.setVisibility(View.VISIBLE);
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)));
                timertw.setText(CheckUtils.parseHourOrMinutes(hours) + ":" + CheckUtils.parseHourOrMinutes(minutes) + ":" + CheckUtils.parseHourOrMinutes(seconds));
            } else {
                timertw.setText("Tempo Scaduto!");
                cancelrunbtn.setVisibility(View.GONE);
                startlivebtn.setVisibility(View.VISIBLE);
            }
        }

    } // End Class View Holder


    public HashMap<Integer,Integer> getMapRunPos(){ return maprunpos;}


    public interface Communicator{
        public void respondDetailRun(int position);
        public void respondStartLive(int position);
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }



}
