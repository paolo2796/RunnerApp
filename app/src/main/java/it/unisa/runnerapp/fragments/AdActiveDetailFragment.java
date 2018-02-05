package it.unisa.runnerapp.fragments;

import android.app.Dialog;
import android.app.Fragment;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
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
    private MapView mapview;
    private TextView starthourtw;
    private TextView datestarttw;
    private TextView estimatedkmtw;
    private TextView estimatedhmtw;
    private Button followersbtn;
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

    public void setCommunicator(Communicator communicator) {

        this.communicator = communicator;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;

        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapview_direction_style_json));
            LatLng hcmus = new LatLng(run.getMeetingPoint().latitude, run.getMeetingPoint().longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 15));
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title("Punto Incontro")
                    .position(hcmus)));

            sendRequest();
        }


        catch(Resources.NotFoundException e){
            Log.e(MESSAGE_LOG, "Mappa non trovata: Errore: ", e);
        }
    }


    private void sendRequest(){

        try {
            new DirectionFinder(this,new LatLng(40.68244079999999,14.76809609999998),run.getMeetingPoint()).execute();
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
            ((TextView) v.findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) v.findViewById(R.id.tvDistance)).setText(route.distance.text);


            Bitmap bitmapicon =  CheckUtils.getBitmapFromVectorDrawable(getActivity(),R.drawable.ic_starthour_24dp);
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapicon))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapicon))
                    .title(route.endAddress)
                    .position(route.endLocation)));


            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
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
