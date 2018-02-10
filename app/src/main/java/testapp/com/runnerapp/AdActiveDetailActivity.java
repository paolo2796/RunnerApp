package testapp.com.runnerapp;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.AdActiveDetailFragment;

/**
 * Created by Paolo on 02/02/2018.
 */

public class AdActiveDetailActivity extends AppCompatActivity {

    private android.app.FragmentManager fm;
    private AdActiveDetailFragment adactivedetailfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adactivedetail);


        // Viene recuperato l'annuncio
        ActiveRun activerun = new ActiveRunDaoImpl().findByID(getIntent().getIntExtra("codrun",-1));
        fm = getFragmentManager();
        adactivedetailfg = AdActiveDetailFragment.newInstance(activerun);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.adactivedetail_container,adactivedetailfg);
        ft.commit();

    }

}
