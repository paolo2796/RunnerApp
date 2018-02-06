package testapp.com.runnerapp;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.fragments.AdActiveDetailFragment;

/**
 * Created by Paolo on 02/02/2018.
 */

public class AdActiveDetailActivity extends AppCompatActivity implements AdActiveDetailFragment.Communicator {

    private android.app.FragmentManager fm;
    private AdActiveDetailFragment adactivedetailfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adactivedetail);

        int codrun = getIntent().getIntExtra("codrun",-1);
        ActiveRun runactive = new ActiveRunDaoImpl().findByID(codrun);


        fm = getFragmentManager();
        adactivedetailfg = AdActiveDetailFragment.newInstance(runactive);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.adactivedetail_container,adactivedetailfg);
        ft.commit();

        adactivedetailfg.setCommunicator(this);

    }

    @Override
    public void respond(int index) {

    }
}
