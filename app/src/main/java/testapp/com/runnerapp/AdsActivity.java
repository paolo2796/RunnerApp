package testapp.com.runnerapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.fragments.AdsActiveFragment;

/**
 * Created by Paolo on 01/02/2018.
 */

public class AdsActivity extends AppCompatActivity implements AdsActiveFragment.Communicator{

    AdsActiveFragment adsactivefrag;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);


        fm = getFragmentManager();

        adsactivefrag = (AdsActiveFragment) fm.findFragmentById(R.id.adscontainer);
        adsactivefrag.setCommunicator(this);


    }


    @Override
    public void respond(int position) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",adsactivefrag.arrayadapter.getItem(position).getId());
        startActivity(intent);

    }



    public void requestParticipation(View v){

        int tag = Integer.parseInt(v.getTag().toString());
       ActiveRun activeruncurren = (ActiveRun) adsactivefrag.arrayadapter.getItem(tag);
        Button participation = v.findViewById(R.id.participatebtn);

        View view = adsactivefrag.arrayadapter.getView(tag,(View) v.getParent(),null);
        Button cancelrun = (Button) view.findViewById(R.id.cancelbtn);
        cancelrun.setVisibility(View.VISIBLE);
        participation.setVisibility(View.GONE);
        new PActiveRunDaoImpl().createParticipationRun(activeruncurren.getId(),"paolo2796");

    }


    public void cancelParticipation(View v){

        int tag = Integer.parseInt(v.getTag().toString());
        Button cancelrun = v.findViewById(R.id.cancelbtn);
        View view = adsactivefrag.arrayadapter.getView(tag,(View) v.getParent(),null);
        Button participation = (Button) view.findViewById(R.id.participatebtn);
        cancelrun.setVisibility(View.GONE);
        participation.setVisibility(View.VISIBLE);
        ActiveRun activeruncurren = (ActiveRun) adsactivefrag.arrayadapter.getItem(tag);
        new PActiveRunDaoImpl().deleteParticipationRun(activeruncurren.getId(),"paolo2796");

    }





}


