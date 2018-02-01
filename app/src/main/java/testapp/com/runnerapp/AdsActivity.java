package testapp.com.runnerapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.AdFragment;

/**
 * Created by Paolo on 01/02/2018.
 */

public class AdsActivity extends AppCompatActivity {


    List<ActiveRun> runsactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        setContentView(R.layout.adsactivity);

        runsactive = new ActiveRunDaoImpl().getAllActiveRuns();

        FragmentManager fm = getFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();

        for(int i=0;i<runsactive.size();i++) {

            Fragment adfrag = new AdFragment();
            this.sendData(adfrag, runsactive.get(i));
            ft.add(R.id.fragcontainer, adfrag);
        }

        ft.commit();
    }


    public void sendData(Fragment adfrag, ActiveRun activerun){

        Bundle args = new Bundle();
        args.putSerializable("activerun",activerun);
        adfrag.setArguments(args);
    }
}
