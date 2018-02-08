package testapp.com.runnerapp;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.adapters.FollowersAdapter;
import it.unisa.runnerapp.adapters.MyAdsAdapater;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.MyAdsFragment;

/**
 * Created by Paolo on 07/02/2018.
 */

public class MyAdsActivity extends AppCompatActivity implements MyAdsFragment.CommunicatorActivity {


    MyAdsFragment myadsfragment;
    FragmentManager fm;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myads);

        fm = getFragmentManager();
        myadsfragment = (MyAdsFragment) fm.findFragmentById(R.id.myadscontainer);
        myadsfragment.setCommunicator(this);

    }


    @Override
    public void respondMyAdsActive(int position) {

        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",myadsfragment.arrayadapter.getItem(position).getId());
        startActivity(intent);

    }
}
