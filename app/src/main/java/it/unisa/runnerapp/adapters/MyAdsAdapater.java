package it.unisa.runnerapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.fragments.MyAdsFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.EditRunActivity;
import testapp.com.runnerapp.MainActivity;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 07/02/2018.
 */

public class MyAdsAdapater extends ArrayAdapter<ActiveRun> {

    private LayoutInflater inflater;
    MyAdsAdapater.Communicator communicator;
    private List<MyAdsAdapater.ViewHolder> lstHolders;
    private Handler mHandler = new Handler();

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (MyAdsAdapater.ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public MyAdsAdapater(@NonNull Context context, int resource, List<ActiveRun> runs) {
        super(context, resource, runs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        ActiveRun activeruncurrent = getItem(position);
        MyAdsAdapater.ViewHolder holder = null;

        if (convertView == null) {
            holder = new MyAdsAdapater.ViewHolder();
            convertView = inflater.inflate(R.layout.row_myads, parent, false);
            holder.starthour = (TextView) convertView.findViewById(R.id.starthour);
            holder.datestart = (TextView) convertView.findViewById(R.id.datestart);
            holder.delayparticipation = (Button) convertView.findViewById(R.id.delayparticipation_btn);
            holder.timertw = (TextView) convertView.findViewById(R.id.timer);
            holder.deleterunbtn = (Button) convertView.findViewById(R.id.deleterun_btn);
            holder.editrunbtn = (Button) convertView.findViewById(R.id.editrun_btn);
            holder.estimatedkmtw = (TextView) convertView.findViewById(R.id.estimatedkm_tw);
            holder.estimatedtimetw = (TextView) convertView.findViewById(R.id.estimatedtime_tw);
            holder.pointmeetingimg = (ImageView) convertView.findViewById(R.id.pointmeeting_img);
            convertView.setTag(holder);
            synchronized (lstHolders) {
                lstHolders.add(holder);
            }
        }
        else {
            holder = (MyAdsAdapater.ViewHolder) convertView.getTag();
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
        Button editrunbtn;
        Button deleterunbtn;
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
            deleterunbtn.setTag(position);
            pointmeetingimg.setTag(position);

            deleterunbtn.setOnTouchListener(getOnTouchListenerDelete(editrunbtn));
            editrunbtn.setOnTouchListener(getOnTouchListnerEdit(deleterunbtn));
            editrunbtn.setTag(position);
            starthour.setText(CheckUtils.convertHMToStringFormat(item.getStartDate()));
            datestart.setText(CheckUtils.convertDateToStringFormat(item.getStartDate()));
            pointmeeting = activerun.getMeetingPoint();
            estimatedtimetw.setText(String.valueOf(item.getEstimatedHours() + " h " + item.getEstimatedMinutes() + "m"));
            estimatedkmtw.setText(String.valueOf(item.getEstimatedKm())  + " km");
            pointmeetingimg.setOnClickListener(getClicklPointMeetingListener());
            Animation animation = AnimationUtils.loadAnimation(MyAdsAdapater.this.getContext(),R.anim.scaling);
            pointmeetingimg.startAnimation(animation);
            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {
            long timeDiff = activerun.getStartDate().getTime() - currentTime;
            if (timeDiff > 0) {
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)));
                timertw.setText(CheckUtils.parseHourOrMinutes(hours) + ":" + CheckUtils.parseHourOrMinutes(minutes) + ":" + CheckUtils.parseHourOrMinutes(seconds));
            } else {
                timertw.setText("Tempo Scaduto!");
                deleterunbtn.setVisibility(View.GONE);
                editrunbtn.setVisibility(View.GONE);
                delayparticipation.setVisibility(View.VISIBLE);
            }
        }

    } // End Class View Holder



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
                    communicator.respondEdit(Integer.valueOf(v.getTag().toString()));
                }
                return true;
            }
        });

    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    public interface Communicator{
        public void respondDetailRun(int position);
        public void respondEdit(int position);
        public void respondConfirmDelete(ActiveRun runtag);
    }

}
