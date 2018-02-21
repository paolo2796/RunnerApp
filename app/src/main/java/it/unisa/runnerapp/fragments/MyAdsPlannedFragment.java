package it.unisa.runnerapp.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.util.List;
import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.adapters.MyAdPlannedAdapter;
import it.unisa.runnerapp.beans.ActiveRun;
import testapp.com.runnerapp.MainActivityPV;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 08/02/2018.
 */

    public class MyAdsPlannedFragment extends Fragment implements MyAdPlannedAdapter.Communicator  {


        List<ActiveRun> runsactive;
        public ListView listview;
        public MyAdPlannedAdapter arrayadapter;
        ActiveRunDao activerundao;
        Dialog dialog;
        CommunicatorActivity communicatoractivity;
        List<ActiveRun> runs;

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.adsgen_fragment, container, false);
            listview = (ListView) v.findViewById(R.id.listview);
            Log.i("CurrentTime",String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()));
            runs = new PActiveRunDaoImpl().findRunActiveByRunner(MainActivityPV.userlogged.getNickname(), "data_inizio",new Timestamp(System.currentTimeMillis()));
            Log.i("Query size",String.valueOf(runs.size()));
            arrayadapter = new MyAdPlannedAdapter(MyAdsPlannedFragment.this.getActivity(), R.layout.row_myadsplanned, runs);
            arrayadapter.setCommunicator(MyAdsPlannedFragment.this);
            listview.setAdapter(arrayadapter);


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



    public static void removeParticipationFirebase(ActiveRun run, String nick){

        DatabaseReference refrun = MainActivityPV.databaseruns.child(String.valueOf(run.getId())).child("participation");
        refrun.child(nick).removeValue();

    }

}

