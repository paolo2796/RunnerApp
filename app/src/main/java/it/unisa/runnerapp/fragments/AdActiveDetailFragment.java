package it.unisa.runnerapp.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.adapters.AdActiveAdapter;
import it.unisa.runnerapp.adapters.FollowersAdapter;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.customwidgets.CustomMap;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.DirectionFinder;
import it.unisa.runnerapp.utils.DirectionFinderListener;
import it.unisa.runnerapp.utils.Route;
import testapp.com.runnerapp.Manifest;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 03/02/2018.
 */

public class AdActiveDetailFragment extends Fragment implements OnMapReadyCallback, DirectionFinderListener {

    private View v;
    Communicator communicator;
    private ActiveRun run;
    public static final String ARG_POSITION = "activerun";
    private static final String MESSAGE_LOG = "MessageAdDetail";
    private static final int COLOR_POLYLINE = Color.YELLOW;

    // Views Component
    private TextView nickmastertw;
    private ImageView masterprofileimg;
    private CustomMap mapview;
    private TextView starthourtw;
    private TextView datestarttw;
    private TextView estimatedkmtw;
    private TextView estimatedhmtw;
    private Button followersbtn;
    private TextView durationtw;
    private TextView distancetw;
    private ListView listview;
    private ArrayAdapter<Runner> arrayadapter;


    // component direction
    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            run = (ActiveRun) getArguments().getSerializable(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.adactivedetail_fragment, container, false);

        //initialize

        mapview = (CustomMap) v.findViewById(R.id.mapview);
        datestarttw = (TextView) v.findViewById(R.id.datestart);
        starthourtw = (TextView) v.findViewById(R.id.starthour);
        estimatedkmtw = (TextView) v.findViewById(R.id.estimatedkm_tw);
        estimatedhmtw = (TextView) v.findViewById(R.id.estimatedhm_tw);
        followersbtn = (Button) v.findViewById(R.id.followers_btn);
        followersbtn.setOnClickListener(getFollowersClickListener());


        starthourtw.setText(CheckUtils.convertHMToStringFormat(run.getStartDate()));
        datestarttw.setText(CheckUtils.convertDateToStringFormat(run.getStartDate()));
        estimatedkmtw.setText(String.valueOf(run.getEstimatedKm()) + " km previsti");
        estimatedhmtw.setText(String.valueOf(run.getEstimatedHours() + "h " + String.valueOf(run.getEstimatedMinutes()) + "m stimati"));
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        return v;
    }

    public void setCommunicator(Communicator communicator) {

        this.communicator = communicator;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;

        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapview_direction_style_json));;
            sendRequest();
        }


        catch(Resources.NotFoundException e){
            Log.e(MESSAGE_LOG, "Mappa non trovata: Errore: ", e);
        }
    }


    private void sendRequest(){

        try {
            new DirectionFinder(this,new LatLng(40.673944,14.770186), run.getMeetingPoint()).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    // Implementazione metodi DirectionFinderListener

    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15));
            durationtw = (TextView) v.findViewById(R.id.duration_tw);
            distancetw = (TextView) v.findViewById(R.id.distance_tw);
            durationtw.setText(route.duration.text);
            distancetw.setText(" / " + route.distance.text);


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.startAddress)
                        .position(route.startLocation)));



            Bitmap bitmapicon =  CheckUtils.getBitmapFromVectorDrawable(getActivity(),R.drawable.ic_destination_35dp);
            MarkerOptions destinationoptionmarker= new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmapicon)).title(route.endAddress).position(route.endLocation);
            destinationMarkers.add(mMap.addMarker(destinationoptionmarker));
            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());


            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(getResources().getColor(R.color.tempv_celestial)).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
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



    public View.OnClickListener getFollowersClickListener(){
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


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){

            LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myContentsView = inflater.inflate(R.layout.custom_info_reach_master, null);
        }

        @Override
        public View getInfoContents(Marker marker) {


            nickmastertw = (TextView) myContentsView.findViewById(R.id.masternickaname_tw);
            masterprofileimg = (ImageView) myContentsView.findViewById(R.id.masterprofile_img);
            masterprofileimg.setImageDrawable(run.getMaster().getProfileImage());
            nickmastertw.setText(run.getMaster().getNickname());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {

            return null;
        }
    } // end class MyInfoWindowAdapter


    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapview.onStop();
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

    public void onCreate() {
        super.onCreate(getArguments());
        mapview.onCreate(getArguments());
    }





}
