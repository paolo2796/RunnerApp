package it.unisa.runnerapp.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.adapters.MyAdPlannedAdapter;
import it.unisa.runnerapp.adapters.MyAdsAdapater;
import it.unisa.runnerapp.beans.ActiveRun;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 08/02/2018.
 */

    public class MyAdsPlannedFragment extends Fragment implements MyAdPlannedAdapter.Communicator  {

        List<ActiveRun> runsactive;
        ListView listview;
        public MyAdPlannedAdapter arrayadapter;
        ActiveRunDao activerundao;
        Dialog dialog;
        CommunicatorActivity communicatoractivity;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.adsgen_fragment, container, false);
            listview = (ListView) v.findViewById(R.id.listview);
            List<ActiveRun> runs = new PActiveRunDaoImpl().findRunByRunner("paolo2796","data_inizio");
            arrayadapter = new MyAdPlannedAdapter(this.getActivity(),R.layout.row_myadsplanned,runs);
            listview.setAdapter(arrayadapter);
            arrayadapter.setCommunicator(this);

            return v;
        }


    @Override
    public void respondDetailRun(int position) {
        communicatoractivity.responMyAdsPlannedDetailRun(position);
    }

    @Override
        public void respondStartLive(int position) {
                communicatoractivity.respondStartLiveActivity(position);
        }


    class ClickConfirmedRunDialog implements View.OnClickListener{

            private ActiveRun run;

            public ClickConfirmedRunDialog(ActiveRun run){this.run = run;}

            @Override
            public void onClick(View v) {


                if(Integer.parseInt(v.getTag().toString())==0){
                    new ActiveRunDaoImpl().deleteActiveRun(run.getId());
                    it.unisa.runnerapp.fragments.MyAdsPlannedFragment.this.arrayadapter.remove(run);
                    it.unisa.runnerapp.fragments.MyAdsPlannedFragment.this.arrayadapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

                else{

                    dialog.dismiss();
                }

            }
        }

        public void setCommunicator(it.unisa.runnerapp.fragments.MyAdsPlannedFragment.CommunicatorActivity communicatoractivity){
            this.communicatoractivity = communicatoractivity;
        }

        public interface CommunicatorActivity{
            public void responMyAdsPlannedDetailRun(int index);
            public void respondStartLiveActivity(int codrun);
        }



    }

