package testapp.com.runnerapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.AdsActiveFragment;
import it.unisa.runnerapp.fragments.AdsFinishedFragment;
import it.unisa.runnerapp.fragments.MyAdsFragment;

/**
 * Created by Paolo on 08/02/2018.
 */

public class MainActivityPV extends AppCompatActivity implements AdsFinishedFragment.CommunicatorActivity, MyAdsFragment.CommunicatorActivity, AdsActiveFragment.Communicator {

    AdsFinishedFragment adsfinishedfragment;
    MyAdsFragment myadsfragment;
    AdsActiveFragment adsactivefragment;
    private FragmentManager fm;
    private BottomBar bottomBar;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mainpv);

            fm = getFragmentManager();



            bottomBar = (BottomBar) findViewById(R.id.bottomBar);
            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.myads_tab) {

                    myadsfragment = new MyAdsFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.containerfragment_frame,myadsfragment);
                    ft.commit();
                    myadsfragment.setCommunicator(MainActivityPV.this);

                }
                else if (tabId == R.id.participate_tab) {

                    adsactivefragment = AdsActiveFragment.newInstance(MainActivityPV.this);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.containerfragment_frame,adsactivefragment);
                    ft.commit();

                }
                else if (tabId == R.id.myadsfinished_tab) {

                    adsfinishedfragment = new AdsFinishedFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.containerfragment_frame,adsfinishedfragment);
                    ft.commit();
                    adsfinishedfragment.setCommunicator(MainActivityPV.this);
                }
            }
        });

    }



    @Override
    public void respondAdsFinished(int position) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",adsfinishedfragment.arrayadapter.getItem(position).getId());
        startActivity(intent);

    }

    @Override
    public void respondAdsActive(int position) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",adsactivefragment.arrayadapter.getItem(position).getId());
        startActivity(intent);

    }

    @Override
    public void respondMyAdsActive(int position) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",myadsfragment.arrayadapter.getItem(position).getId());
        startActivity(intent);

    }

    public void requestParticipation(View v){

        int tag = Integer.parseInt(v.getTag().toString());
        ActiveRun activeruncurren = (ActiveRun) adsactivefragment.arrayadapter.getItem(tag);
        Button participation = v.findViewById(R.id.participatebtn);
        View view = adsactivefragment.arrayadapter.getView(tag,(View) v.getParent(),null);
        Button cancelrun = (Button) view.findViewById(R.id.cancelbtn);
        cancelrun.setVisibility(View.VISIBLE);
        participation.setVisibility(View.GONE);
        new PActiveRunDaoImpl().createParticipationRun(activeruncurren.getId(),"paolo2796");

    }


    public void cancelParticipation(View v){

        int tag = Integer.parseInt(v.getTag().toString());
        Button cancelrun = v.findViewById(R.id.cancelbtn);
        View view = adsactivefragment.arrayadapter.getView(tag,(View) v.getParent(),null);
        Button participation = (Button) view.findViewById(R.id.participatebtn);
        cancelrun.setVisibility(View.GONE);
        participation.setVisibility(View.VISIBLE);
        ActiveRun activeruncurren = (ActiveRun) adsactivefragment.arrayadapter.getItem(tag);
        new PActiveRunDaoImpl().deleteParticipationRun(activeruncurren.getId(),"paolo2796");

    }
}
