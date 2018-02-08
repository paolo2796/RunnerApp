package testapp.com.runnerapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.AdActiveDetailFragment;
import it.unisa.runnerapp.fragments.AdsFinishedFragment;
import it.unisa.runnerapp.fragments.MyAdsFragment;

/**
 * Created by Paolo on 07/02/2018.
 */

public class AdsFinishedActivity extends AppCompatActivity implements AdsFinishedFragment.CommunicatorActivity{


    private android.app.FragmentManager fm;
    private AdsFinishedFragment adsfinishedfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myadsfinished);

        adsfinishedfragment = AdsFinishedFragment.newInstance();
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.adsfinished_container,adsfinishedfragment);
        ft.commit();

        adsfinishedfragment.setCommunicator(this);

    }


    @Override
    public void respondAdsFinished(int position) {
        Intent intent = new Intent(this,AdActiveDetailActivity.class);
        intent.putExtra("codrun",adsfinishedfragment.arrayadapter.getItem(position).getId());
        startActivity(intent);
    }
}
