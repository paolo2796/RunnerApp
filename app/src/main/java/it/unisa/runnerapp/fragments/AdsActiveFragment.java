package it.unisa.runnerapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.adapters.AdActiveAdapter;
import it.unisa.runnerapp.beans.ActiveRun;
import testapp.com.runnerapp.MainActivityPV;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 03/02/2018.
 */


public class AdsActiveFragment extends Fragment implements AdActiveAdapter.Communicator {

     List<ActiveRun> runsactive;
     ListView listview;
     public AdActiveAdapter arrayadapter;
        AdsActiveFragment.CommunicatorActivity communicatoractivity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adsgen_fragment, container, false);

        listview = (ListView) v.findViewById(R.id.listview);
        List<ActiveRun> runs = new ActiveRunDaoImpl().getAvailableRunsWithin24hByRunner("paolo2796","data_inizio");
        Log.i("Messaggio",String.valueOf(runs.size()));

        arrayadapter = new AdActiveAdapter(this.getActivity(),R.layout.row_adactive,runs);
        arrayadapter.setCommunicator(this);
        listview.setAdapter(arrayadapter);
        return v;
    }


    public void setCommunicator(AdsActiveFragment.CommunicatorActivity communicatoractivity){
        this.communicatoractivity = communicatoractivity;
    }

    @Override
    public void respondDetailRun(int index) {
        communicatoractivity.responAdsActiveDetailRun(index);
    }

    public interface CommunicatorActivity {
        public void responAdsActiveDetailRun(int index);
    }

    public static AdsActiveFragment newInstance(AdsActiveFragment.CommunicatorActivity communicator){
        AdsActiveFragment adsActiveFragment = new AdsActiveFragment();
        adsActiveFragment.setCommunicator(communicator);
        return adsActiveFragment;

    }


}
