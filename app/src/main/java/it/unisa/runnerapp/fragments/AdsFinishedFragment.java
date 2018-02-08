package it.unisa.runnerapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.FinishedRunDaoImpl;
import it.unisa.runnerapp.adapters.AdActiveAdapter;
import it.unisa.runnerapp.adapters.AdFinishedAdapter;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.FinishedRun;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 07/02/2018.
 */

public class AdsFinishedFragment extends Fragment implements AdFinishedAdapter.Communicator {


    List<ActiveRun> runsactive;
    ListView listview;
    public AdFinishedAdapter arrayadapter;
    AdsFinishedFragment.CommunicatorActivity communicatoractivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adsgen_fragment, container, false);


        listview = (ListView) v.findViewById(R.id.listview);
        List<FinishedRun> runs = new FinishedRunDaoImpl().findByRunnerWithoutMaster("paolo2796","data_inizio");
        arrayadapter = new AdFinishedAdapter(this.getActivity(),R.layout.row_myadsfinished,runs);
        listview.setAdapter(arrayadapter);
        arrayadapter.setCommunicator(this);

        return v;
    }


    @Override
    public void respond(int position) {
        communicatoractivity.respondAdsFinished(position);

    }


    public void setCommunicator(AdsFinishedFragment.CommunicatorActivity communicatoractivity){
        this.communicatoractivity = communicatoractivity;
    }


    public interface CommunicatorActivity{

        public void respondAdsFinished(int index);
    }

    public static AdsFinishedFragment newInstance(){
        return new AdsFinishedFragment();
    }

}
