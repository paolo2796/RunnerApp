package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.CheckPermissionActivity;
import testapp.com.runnerapp.MainActivityPV;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 02/02/2018.
 */


public class AdActiveAdapter extends ArrayAdapter<ActiveRun> {
    private LayoutInflater inflater;
    AdActiveAdapter.Communicator communicator;
    private List<AdActiveAdapter.ViewHolder> lstHolders;
    private Handler mHandler = new Handler();
    private HashMap<Integer,Integer> maprunpos;

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
        maprunpos = new HashMap<>();
        startUpdateTimer();
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        ActiveRun activeruncurrent = getItem(position);

        AdActiveAdapter.ViewHolder holder = null;

        if (convertView == null) {
            maprunpos.put(activeruncurrent.getId(),position);
            holder = new AdActiveAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.row_adactive, parent, false);
            holder.starthour = (TextView) convertView.findViewById(R.id.starthour);
            holder.datestart = (TextView) convertView.findViewById(R.id.datestart);
            holder.timertw = (TextView) convertView.findViewById(R.id.timer);
            holder.participationbtn = (Button) convertView.findViewById(R.id.participationbtn);
            holder.delayparticipation = (Button) convertView.findViewById(R.id.delayparticipation_btn);
            holder.estimatedkmtw = (TextView) convertView.findViewById(R.id.estimatedkm_tw);
            holder.estimatedtimetw = (TextView) convertView.findViewById(R.id.estimatedtime_tw);
            holder.pointmeetingimg = (ImageView) convertView.findViewById(R.id.pointmeeting_img);
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
                ActiveRun activeruncurren = (ActiveRun) getItem(tag);
                Toast.makeText(getContext(),"Parteciperai a questa gara! Vai in sezione 'Programmate'",Toast.LENGTH_LONG).show();
                new PActiveRunDaoImpl().createParticipationRun(activeruncurren.getId(),MainActivityPV.userlogged.getNickname());

                saveParticipationFirebase(activeruncurren,MainActivityPV.userlogged.getNickname());
                MainActivityPV.databaseruns.child(String.valueOf(activeruncurren.getId())).child("participation");
                AdActiveAdapter.this.remove(activeruncurren);
                AdActiveAdapter.this.notifyDataSetChanged();
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


    private class ViewHolder {

        TextView datestart;
        TextView starthour;
        TextView timertw;
        Button participationbtn;
        Button delayparticipation;
        ActiveRun activerun;
        LatLng pointmeeting;
        TextView estimatedkmtw;
        TextView estimatedtimetw;
        ImageView pointmeetingimg;

        int position;

        public void setData(ActiveRun item, int position) {

            activerun = item;
            this.position = position;
            participationbtn.setTag(position);
            delayparticipation.setTag(position);
            pointmeetingimg.setTag(position);
            participationbtn.setOnClickListener(getRequestParicipation());
            starthour.setText(CheckUtils.convertHMToStringFormat(item.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(item.getStartDate()));
            pointmeeting = activerun.getMeetingPoint();
            estimatedtimetw.setText(String.valueOf(item.getEstimatedHours() + " h " + item.getEstimatedMinutes() + "m"));
            estimatedkmtw.setText(String.valueOf(item.getEstimatedKm()));
            pointmeetingimg.setOnClickListener(getClicklPointMeetingListener());
            Animation animation = AnimationUtils.loadAnimation(AdActiveAdapter.this.getContext(),R.anim.scaling);
            pointmeetingimg.startAnimation(animation);
            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {
            long timeDiff = activerun.getStartDate().getTime() - currentTime;
            if (timeDiff > 0) {
                delayparticipation.setVisibility(View.GONE);
                participationbtn.setVisibility(View.VISIBLE);
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)));
                timertw.setText(CheckUtils.parseHourOrMinutes(hours) + ":" + CheckUtils.parseHourOrMinutes(minutes) + ":" + CheckUtils.parseHourOrMinutes(seconds));
            } else {
                timertw.setText("Tempo Scaduto!");
                participationbtn.setVisibility(View.GONE);
                delayparticipation.setVisibility(View.VISIBLE);
            }
        }


    } // End Class View Holder


    public void setCommunicator(AdActiveAdapter.Communicator communicator) {this.communicator = communicator;}

    public interface Communicator{public void respondDetailRun(int index);}



    public void saveParticipationFirebase(ActiveRun run, String nick){

        DatabaseReference refrun = MainActivityPV.databaseruns.child(String.valueOf(run.getId())).child("participation");
        refrun.child(nick).setValue(nick);
    }

    public HashMap<Integer,Integer> getMapRunPos(){ return maprunpos;}

}
