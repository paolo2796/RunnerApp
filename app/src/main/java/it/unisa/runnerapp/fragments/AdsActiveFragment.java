package it.unisa.runnerapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.adapters.AdActiveAdapter;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;
import testapp.com.runnerapp.AddNoticeActivity;
import testapp.com.runnerapp.CheckPermissionActivity;
import testapp.com.runnerapp.MainActivityPV;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 03/02/2018.
 */


public class AdsActiveFragment extends Fragment implements AdActiveAdapter.Communicator {
    // DB Firebase
    public static  DatabaseReference databaserunners = CheckPermissionActivity.participationdb.getReference("Runs");
    private GeoFire geofire;
    private GeoQuery geoquery;


    private static int MINTIME = 10000;
    private static int MINDISTANCE = 50;
    private static String MESSAGE_LOG = "Messaggio AdsActiveF";



    public AdActiveAdapter arrayadapter;
    private AdsActiveFragment.CommunicatorActivity communicatoractivity;
    private List<ActiveRun> runs;
    private LocationListener mylocationlistener;
    private Location myposition;

    //Component View
    private ListView listview;
    private Button addnoticebtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        runs = new ArrayList<ActiveRun>();

        mylocationlistener = getMyLocationListener();

        ((MainActivityPV) getActivity()).checkManifestPermission();
        ((MainActivityPV) getActivity()).getLocationmanager().requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDISTANCE, mylocationlistener);
        geofire = new GeoFire(databaserunners);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adsactive_fragment, container, false);

        listview = (ListView) v.findViewById(R.id.listview);
        addnoticebtn = (Button) v.findViewById(R.id.addrun_btn);
        arrayadapter = new AdActiveAdapter(this.getActivity(), R.layout.row_adactive, runs);
        arrayadapter.setCommunicator(this);
        listview.setAdapter(arrayadapter);
        addnoticebtn.setOnClickListener(getOnClickAddNoticeListener());
        return v;
    }


    public void setCommunicator(AdsActiveFragment.CommunicatorActivity communicatoractivity) {
        this.communicatoractivity = communicatoractivity;
    }

    @Override
    public void respondDetailRun(int index) {
        communicatoractivity.responAdsActiveDetailRun(index);
    }

    public interface CommunicatorActivity {
        public void responAdsActiveDetailRun(int index);
    }

    public static AdsActiveFragment newInstance(AdsActiveFragment.CommunicatorActivity communicator) {
        AdsActiveFragment adsActiveFragment = new AdsActiveFragment();
        adsActiveFragment.setCommunicator(communicator);
        return adsActiveFragment;
    }


    public View.OnClickListener getOnClickAddNoticeListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNoticeActivity.class));

            }
        };
    }


    public LocationListener getMyLocationListener() {

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(MESSAGE_LOG, String.valueOf(location.getLatitude() + "-" + location.getLongitude()));
                if(geoquery==null) {
                    Log.i("Messaggio","SONO DENTRO");
                    myposition = location;
                    queryAtLocation();
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }


    public void queryAtLocation(){

        geoquery = geofire.queryAtLocation(new GeoLocation(myposition.getLatitude(),myposition.getLongitude()),12);
        geoquery.addGeoQueryDataEventListener(getGeoQueryDataEventListener());


    }// End method



    public GeoQueryDataEventListener getGeoQueryDataEventListener(){

        return new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                Log.i("onDataEntered",dataSnapshot.getKey());
                Long datestart = dataSnapshot.child("datestart").getValue(Long.class);
                Log.i(MESSAGE_LOG,datestart.toString() + "- "+ String.valueOf(System.currentTimeMillis()));

                if(datestart>=System.currentTimeMillis()){
                    Map<String, String> td = (HashMap<String,String>) dataSnapshot.child("participation").getValue();
                    Set list = td.keySet();
                    Iterator iter = list.iterator();
                    boolean istrue = false;

                    // Verifico se l'utente loggato sta già partecipando a questa gara
                    while(iter.hasNext() && !istrue) {
                        Object key = iter.next();
                        String value = td.get(key);
                        if(td.get(key).equals("paolo2796")){

                            istrue=true;

                            Log.i(MESSAGE_LOG,"STO PARTECIPANDO!");
                        }

                    } // End while


                    if(!istrue){
                        Log.i(MESSAGE_LOG,"NON STO PARTECIPANDO");
                        // Inserisci gara all'interno della sezione partecipa
                        ActiveRun activeRun = new ActiveRunDaoImpl().findByID(Integer.valueOf(dataSnapshot.getKey()));
                        arrayadapter.add(activeRun);
                        arrayadapter.notifyDataSetChanged();
                    }
                } //End if
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

                Log.i("onDataExited",dataSnapshot.getKey());

                Long datestart = dataSnapshot.child("datestart").getValue(Long.class);
                if(datestart>=System.currentTimeMillis()){

                    //rimuovi gara dalla sezione partecipa
                    Map<String, String> td = (HashMap<String,String>) dataSnapshot.child("participation").getValue();
                    Set list = td.keySet();
                    Iterator iter = list.iterator();
                    boolean istrue = false;
                    // Verifico se l'utente loggato sta già partecipando a questa gara
                    while(iter.hasNext() && !istrue) {
                        Object key = iter.next();
                        String value = td.get(key);
                        if(td.get(key).equals("paolo2796")){

                            istrue=true;

                            Log.i(MESSAGE_LOG,"STO PARTECIPANDO!");
                        }

                    } // End while


                    if(!istrue){
                        Log.i(MESSAGE_LOG,"NON STO PARTECIPANDO");
                        // Inserisci gara all'interno della sezione partecipa

                        arrayadapter.remove(arrayadapter.getItem(arrayadapter.getMapRunPos().get(Integer.valueOf(dataSnapshot.getKey()))));
                        arrayadapter.notifyDataSetChanged();
                    }
                } //End if

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.i("onDataChanged", dataSnapshot.getKey());
                Long datestart = dataSnapshot.child("datestart").getValue(Long.class);
                Map<String, String> td = (HashMap<String, String>) dataSnapshot.child("participation").getValue();
                int idrun = Integer.parseInt(dataSnapshot.getKey());


                /* PER ORA NON SERVE
                Log.i("EVAIIII",arrayadapter.getMapRunPos().get(idrun)!=null? "SONO DIVERSO DA NULL":"SONO NULL");
                if(arrayadapter.getMapRunPos().get(idrun)==null) {
                    // Inserisci gara all'interno della sezione partecipa
                    ActiveRun activeRun = new ActiveRunDaoImpl().findByID(idrun);
                    arrayadapter.add(activeRun);
                    arrayadapter.notifyDataSetChanged();
                } */

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        };
    }

    public static void saveParticipationFirebase(ActiveRun run, String nick){

        DatabaseReference refrun = databaserunners.child(String.valueOf(run.getId())).child("participation");
        refrun.child(nick).setValue(nick);
    }



}
