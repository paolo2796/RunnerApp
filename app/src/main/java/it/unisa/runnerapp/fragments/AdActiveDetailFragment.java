package it.unisa.runnerapp.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.adapters.FollowersAdapter;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.customwidgets.CustomMap;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 03/02/2018.
 */

public class AdActiveDetailFragment extends Fragment implements OnMapReadyCallback {

    Communicator communicator;
    private ActiveRun run;
    public static final String ARG_POSITION = "activerun";
    private static final String MESSAGE_LOG = "Message";

    // Views Component
    private TextView nickmastertw;
    private ImageView masterprofileimg;
    private MapView mapview;
    private TextView starthourtw;
    private TextView datestarttw;
    private TextView estimatedkmtw;
    private TextView estimatedhmtw;
    private Button followersbtn;
    private ListView listview;
    private ArrayAdapter<Runner> arrayadapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            run = (ActiveRun) getArguments().getSerializable(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adactivedetail_fragment, container, false);

        //initialize
        nickmastertw = (TextView) v.findViewById(R.id.masternickaname_tw);
        masterprofileimg = (ImageView) v.findViewById(R.id.masterprofile_img);
        mapview = (MapView) v.findViewById(R.id.mapview);
        datestarttw = (TextView) v.findViewById(R.id.datestart);
        starthourtw = (TextView) v.findViewById(R.id.starthour);
        estimatedkmtw = (TextView) v.findViewById(R.id.estimatedkm_tw);
        estimatedhmtw = (TextView) v.findViewById(R.id.estimatedhm_tw);
        followersbtn = (Button) v.findViewById(R.id.followers_btn);
        followersbtn.setOnClickListener(getClickListener());

        masterprofileimg.setImageDrawable(run.getMaster().getProfileImage());
        nickmastertw.setText(run.getMaster().getNickname());
        starthourtw.setText(CheckUtils.convertHMToStringFormat(run.getStartDate()));
        datestarttw.setText(CheckUtils.convertDateToStringFormat(run.getStartDate()));
        estimatedkmtw.setText(String.valueOf(run.getEstimatedKm()) + " km previsti");
        estimatedhmtw.setText(String.valueOf(run.getEstimatedHours() + "h " + String.valueOf(run.getEstimatedMinutes()) + "m stimati"));
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        return v;
    }

    public void setCommunicator(Communicator communicator){

        this.communicator = communicator;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
           boolean success =  googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapview_direction_style_json));
        }
        catch(Resources.NotFoundException e){
            Log.e(MESSAGE_LOG, "Mappa non trovata: Errore: ", e);
        }
    }

    public interface Communicator{

        public void respond(int index);
    }

    public static AdActiveDetailFragment newInstance(ActiveRun run) {
        AdActiveDetailFragment myFragment = new AdActiveDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSITION, run);
        myFragment.setArguments(args);
        return myFragment;

    }



    public View.OnClickListener getClickListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_dialog_followers);
                dialog.setTitle("Title...");


                listview = (ListView) dialog.findViewById(R.id.listview);
                arrayadapter = new FollowersAdapter(dialog.getContext(),R.layout.row_follower,new PActiveRunDaoImpl().findRunnerByRun(run.getId()));

                listview.setAdapter(arrayadapter);
                dialog.show();
            }
        };

    }



    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

}
