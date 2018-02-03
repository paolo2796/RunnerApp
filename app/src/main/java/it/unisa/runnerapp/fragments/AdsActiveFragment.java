package it.unisa.runnerapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
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
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 03/02/2018.
 */


public class AdsActiveFragment extends Fragment {

     List<ActiveRun> runsactive;
     ListView listview;
     public AdActiveAdapter arrayadapter;
     ActiveRunDao activerundao;
     Communicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adsactive_fragment, container, false);

        listview = (ListView) v.findViewById(R.id.listview);
        List<ActiveRun> runs = new ActiveRunDaoImpl().getActiveRunsWithin24h("data_inizio");
        arrayadapter = new AdActiveAdapter(this.getActivity(),R.layout.row_adactive,runs);
        listview.setAdapter(arrayadapter);
        return v;
    }


    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
        arrayadapter.setCommunicator(communicator);

    }

    public void onInfoWindowClick(int position){

        communicator.respond(position);
    }

    public interface Communicator {
        public void respond(int index);
    }


}
