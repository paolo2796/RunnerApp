package testapp.com.runnerapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.Request_LiveDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.adapters.MyAdPlannedAdapter;
import it.unisa.runnerapp.adapters.MyAdsAdapater;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.RequestLive;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.fragments.MyAdsFinishedFragment;
import it.unisa.runnerapp.fragments.MyAdsFinishedFragment;
import it.unisa.runnerapp.fragments.MyAdsPlannedFragment;
import it.unisa.runnerapp.fragments.MyAdsFragment;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.ConnectionUtil;
import it.unisa.runnerapp.utils.DirectionFinder;
import it.unisa.runnerapp.utils.DirectionFinderImpl;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;

/**
 * Created by Paolo on 08/02/2018.
 */

public class MainActivityPV extends CheckPermissionActivity implements MyAdsPlannedFragment.CommunicatorActivity, MyAdsFragment.CommunicatorActivity,AdsActiveFragment.CommunicatorActivity, MyAdsFinishedFragment.CommunicatorActivity{

    // DB Firebase
    public static FirebaseApp firebaseapp;
    public static FirebaseDatabase firebasedatabase;
    public static DatabaseReference databaseruns;

    // utente loggato
    public static Runner userlogged;

    MyAdsFinishedFragment myadsfinishedfragment;
    MyAdsFragment myadsfragment;
    AdsActiveFragment adsactivefragment;
    MyAdsPlannedFragment myadsplannedfragment;
    FragmentManager fm;


    //Component View
    BottomBar bottomBar;
    CircleImageView myprofileimg;
    Animation animscalingprofile;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mainpv);

            fm = getFragmentManager();

            //Set component view
            bottomBar = (BottomBar) findViewById(R.id.bottomBar);
            myprofileimg = (CircleImageView) findViewById(R.id.myprofileimg);

            //Set listeners
            myprofileimg.setOnClickListener(getClickMyProfileListener());



            bottomBar.setOnTabSelectListener(getTabSelectListener());
            initFire();

    }

    public void initFire(){

        if(firebaseapp==null) {
            firebaseapp = FirebaseUtils.getFirebaseApp(this.getApplicationContext(), RunnersDatabases.PARTICIPATION_API_KEY, RunnersDatabases.PARTICIPATION_APP_ID, RunnersDatabases.PARTICIPATION_DB_URL, RunnersDatabases.PARTICIPATION_DB_NAME);
            firebasedatabase = FirebaseUtils.connectToDatabase(firebaseapp);
            databaseruns = firebasedatabase.getReference(RunnersDatabases.PARTICIPATION_DB_ROOT);
            Log.i("Firebase",databaseruns.toString());
        }
    }


    public View.OnClickListener getClickMyProfileListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivityPV.this,ProfileActivity.class);
                startActivity(intent);

            }
        };
    }


    public OnTabSelectListener getTabSelectListener() {

       return new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                if (tabId == R.id.myads_tab) {

                    myadsfragment = new MyAdsFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.containerfragment_frame, myadsfragment);
                    ft.commit();

                    myadsfragment.setCommunicator(MainActivityPV.this);

                }
                else if (tabId == R.id.participate_tab) {

                    adsactivefragment = AdsActiveFragment.newInstance(MainActivityPV.this);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.containerfragment_frame, adsactivefragment);
                    ft.commit();


                }
                else if (tabId == R.id.myadsfinished_tab) {

                    myadsfinishedfragment = new MyAdsFinishedFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.containerfragment_frame, myadsfinishedfragment);
                    ft.commit();
                    myadsfinishedfragment.setCommunicatoractivity(MainActivityPV.this);
                }
                else if(tabId == R.id.myplanned_tab){
                    myadsplannedfragment = new MyAdsPlannedFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.containerfragment_frame, myadsplannedfragment);
                    ft.commit();
                    myadsplannedfragment.setCommunicator(MainActivityPV.this);

                }
            }
        };
    }


    // Communicator MyAdsFragment

    @Override
    public void respondMyAdsDetailRun(int index) {
        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",myadsfragment.arrayadapter.getItem(index).getId());
        startActivity(intent);
    }

    @Override
    public void respondMyAdsEditRun(int index) {

        Intent intent = new Intent(this,EditRunActivity.class);
        intent.putExtra("codrun",myadsfragment.arrayadapter.getItem(index).getId());
        Log.i("Messaggio",String.valueOf(intent.getIntExtra("codrun",-1)));
        startActivity(intent);
    }


    /* Communicator MyAdsPlannedFragment */
   @Override
    public void responMyAdsPlannedDetailRun(int index) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",myadsplannedfragment.arrayadapter.getItem(index).getId());
        startActivity(intent);
    }


   @Override
    public void respondStartLiveActivity(int codrun) {

        Toast.makeText(this,"AVVIARE START LIVE",Toast.LENGTH_LONG).show();
    }



    /* Communicator AdsActiveFragment */
   @Override
    public void responAdsActiveDetailRun(int index) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",adsactivefragment.arrayadapter.getItem(index).getId());
        startActivity(intent);

    }

    @Override
    public void respondAddNotice(Location myposition){
        Intent intent = new Intent(this,AddNoticeActivity.class);

       if(myposition!=null){
           intent.putExtra("mylatitude",myposition.getLatitude());
           intent.putExtra("mylongitude",myposition.getLongitude());
           startActivity(intent);
        }

        else{

            startActivity(intent);
       }

    }

    @Override
    public void responMyFinishedDetailRun(int index) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",myadsfinishedfragment.arrayadapter.getItem(index).getId());
        startActivity(intent);

    }
}
