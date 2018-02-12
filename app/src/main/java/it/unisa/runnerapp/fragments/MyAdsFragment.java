package it.unisa.runnerapp.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.adapters.MyAdsAdapater;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.utils.ConnectionUtil;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 03/02/2018.
 */
public class MyAdsFragment extends Fragment implements MyAdsAdapater.Communicator  {

    List<ActiveRun> runsactive;
    ListView listview;
    public MyAdsAdapater arrayadapter;
    ActiveRunDao activerundao;
    Dialog dialog;
    MyAdsFragment.CommunicatorActivity communicatoractivity;
    List<ActiveRun> runs;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        runs = new ActiveRunDaoImpl().findByRunnerWithin24hWithoutMaster("paolo2796","data_inizio");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adsgen_fragment, container, false);
        listview = (ListView) v.findViewById(R.id.listview);
        arrayadapter = new MyAdsAdapater(this.getActivity(),R.layout.row_myads,runs);
        listview.setAdapter(arrayadapter);
        arrayadapter.setCommunicator(this);
        return v;
    }

    @Override
    public void respondDetailRun(int position) {
        communicatoractivity.respondMyAdsDetailRun(position);

    }

    @Override
    public void respondEdit(int position) {

    }

    @Override
    public void respondConfirmDelete(ActiveRun runtag) {

        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_confirm_deleterun);

        Button yesbtn = (Button) dialog.findViewById(R.id.yes_btn);
        yesbtn.setTag(0);
        Button nobtn = (Button) dialog.findViewById(R.id.no_btn);
        nobtn.setTag(1);
        yesbtn.setOnClickListener(new MyAdsFragment.ClickConfirmedRunDialog(runtag));
        nobtn.setOnClickListener(new MyAdsFragment.ClickConfirmedRunDialog(runtag));
        dialog.show();

    }









    class ClickConfirmedRunDialog implements View.OnClickListener{

        private ActiveRun run;

        public ClickConfirmedRunDialog(ActiveRun run){this.run = run;}

        @Override
        public void onClick(View v) {


            if(Integer.parseInt(v.getTag().toString())==0){
                new ActiveRunDaoImpl().deleteActiveRun(run.getId());
                MyAdsFragment.this.arrayadapter.remove(run);
                MyAdsFragment.this.arrayadapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            else{

                dialog.dismiss();
            }

        }
    }


    public void setCommunicator(MyAdsFragment.CommunicatorActivity communicatoractivity){
        this.communicatoractivity = communicatoractivity;
    }



    public interface CommunicatorActivity{

        public void respondMyAdsDetailRun(int index);
    }







}
