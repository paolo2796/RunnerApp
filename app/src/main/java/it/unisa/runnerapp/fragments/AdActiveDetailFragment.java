package it.unisa.runnerapp.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.customwidgets.CustomMap;
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
    private CustomMap mapview;


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
        mapview = (CustomMap) v.findViewById(R.id.mapview);


        masterprofileimg.setImageDrawable(run.getMaster().getProfileImage());
        nickmastertw.setText(run.getMaster().getNickname());
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



    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
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
