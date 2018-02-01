package it.unisa.runnerapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.Dao.Interf.RunnerDao;
import it.unisa.runnerapp.adapters.LiveRequestsAdapter;
import it.unisa.runnerapp.adapters.MarkerWindowAdapter;
import it.unisa.runnerapp.beans.GeoUser;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.GeoUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;
import testapp.com.runnerapp.MainActivity;
import testapp.com.runnerapp.R;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback
{
    private Context          ctx;

    private GeoUser          gUser;

    private LocationManager  lManager;
    private LocationProvider lProvider;
    private Location         bestLocation;

    private GoogleMap              gMap;
    private LatLng                 userPosition;
    private HashMap<String,Marker> nearbyRunners;

    private ListView            inboxRequestsListView;
    private LiveRequestsAdapter inboxRequestsAdapter;

    private FirebaseDatabase  locationsDB;
    private FirebaseDatabase  liveRequestsDB;
    private DatabaseReference userLocationReference;

    private GeoFire               gFire;
    private GeoQueryEventListener nearbyRunnersListener;

    private static final double RUNNERS_RESEARCH_RADIUS=0.8;

    private static final int   TIME_UPDATES=650;
    private static final int   DISTANCE_UPDATES=1;
    private static final float ZOOM_CAMERA=19.0f;

    private static final String LOCATION_DEBUG_KEY="Location Update";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ctx = getContext();

        nearbyRunners=new HashMap<>();

        lManager = GeoUtils.getLocationManager(ctx);
        lProvider = GeoUtils.getBestProvider(lManager);

        locationsDB=FirebaseDatabase.getInstance();
        FirebaseApp liveRequestsApp=FirebaseUtils.getFirebaseApp(getContext(),
                RunnersDatabases.LIVE_REQUEST_APP_ID,
                RunnersDatabases.LIVE_REQUEST_API_KEY,
                RunnersDatabases.LIVE_REQUEST_DB_URL,
                RunnersDatabases.LIVE_REQUEST_DB_NAME);
        liveRequestsDB=FirebaseUtils.connectToDatabase(liveRequestsApp);

        //Inizializzazione Adapter
        inboxRequestsAdapter.setDatabase(liveRequestsDB);
        inboxRequestsAdapter.setUser(MainActivity.user.getNickname());

        registerRunnerForRequests();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        try
        {
            LocationListener locationListener = getLocationListener();
            GeoUtils.startLocationUpdates(lManager, LocationManager.GPS_PROVIDER, TIME_UPDATES,DISTANCE_UPDATES, locationListener);
        }
        catch (SecurityException ex)
        {
            //Permessi non approvati dall'utente
            //reagire di conseguenza
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=super.onCreateView(inflater,container,savedInstanceState);
        super.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        try
        {
            gMap=googleMap;
            gMap.setMyLocationEnabled(true);
            gMap.setInfoWindowAdapter(new MarkerWindowAdapter(getContext()));
            gMap.setOnInfoWindowLongClickListener(getInfoWindowClickListener());
            Location location=lManager.getLastKnownLocation(lProvider.getName());
            if(location!=null)
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),ZOOM_CAMERA));
        }
        catch (SecurityException ex)
        {
            //Permessi non approvati dall'utente
            //reagire di conseguenza
        }
    }

    private LocationListener getLocationListener()
    {
        return new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                if(GeoUtils.isBetterLocation(location,bestLocation))
                {
                    bestLocation=location;
                    //Se la mappa non è stata inizializzata vengono creati i dati per farlo
                    //e viene ottenuto il riferimento al nodo figlio che rappresenta l'utente
                    //nella base di dati di firebase
                    if(gUser==null)
                    {
                        gUser=new GeoUser(MainActivity.user.getNickname());
                        //Si procede ad ottenere il root di tutti i runner nel db
                        userLocationReference=locationsDB.getReference(RunnersDatabases.USER_LOCATIONS_DB_ROOT);
                        gFire=new GeoFire(userLocationReference);
                        //al nodo rappresentante l'utente viene associato il nickname come chiave
                        userLocationReference=userLocationReference.child(gUser.getNickname());
                    }

                    //Creazione query per la ricerca dei runners nel raggio specificato
                    GeoQuery gSearch=gFire.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()),RUNNERS_RESEARCH_RADIUS);
                    if(nearbyRunnersListener==null)
                        nearbyRunnersListener=getGeoQueryEventListener();
                    gSearch.addGeoQueryEventListener(nearbyRunnersListener);

                    //Aggiornamento posizione utente in locale
                    gUser.setLatitude(location.getLatitude());
                    gUser.setLongitude(location.getLongitude());
                    //Aggiornamento posizione utente in firebase in modo tale che poi è possibile
                    //recuperare i runners nelle vicinanze tramite le API GeoFire
                    gFire.setLocation(gUser.getNickname(),new GeoLocation(gUser.getLatitude(),gUser.getLongitude()));

                    //Aggiornamento dati mostrati sulla mappa
                    userPosition=new LatLng(location.getLatitude(),location.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition,ZOOM_CAMERA));
                }
                else
                    Log.d(LOCATION_DEBUG_KEY,"Ricevuto fix peggiore della locazione corrente");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {

            }

            @Override
            public void onProviderEnabled(String s)
            {

            }

            @Override
            public void onProviderDisabled(String s)
            {

            }
        };
    }

    private GeoQueryEventListener getGeoQueryEventListener()
    {
        return new GeoQueryEventListener()
        {

            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {
                if(!key.equals(gUser.getNickname())&&nearbyRunners.get(key)==null)
                {
                    Marker marker=gMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude,location.longitude)));
                    nearbyRunners.put(key,marker);
                }
            }

            @Override
            public void onKeyExited(String key)
            {
                Marker marker=nearbyRunners.get(key);
                if(marker!=null)
                {
                    marker.remove();
                    nearbyRunners.remove(key);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location)
            {
                if(!key.equals(gUser.getNickname()))
                {
                    Marker marker=nearbyRunners.get(key);
                    marker.remove();
                    nearbyRunners.remove(key);
                    marker=gMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude,location.longitude)));
                    nearbyRunners.put(key,marker);
                }
            }

            @Override
            public void onGeoQueryReady()
            {
            }

            @Override
            public void onGeoQueryError(DatabaseError error)
            {
            }
        };
    }

    private GoogleMap.OnInfoWindowLongClickListener getInfoWindowClickListener()
    {
        //Alert Dialog di conferma alla pressione lunga dell'info window
        //associato ad un marker
        return new GoogleMap.OnInfoWindowLongClickListener()
        {
            @Override
            public void onInfoWindowLongClick(Marker marker)
            {
                AlertDialog.OnClickListener dialogListener=getDialogClickListener(marker);
                AlertDialog sendRequestDialog=new AlertDialog.Builder(getContext())
                        .setMessage(R.string.live_run_request_ad_message)
                        .setPositiveButton(R.string.live_run_request_ad_positive_button,dialogListener)
                        .setNegativeButton(R.string.live_run_request_ad_negative_button,dialogListener)
                        .show();
            }
        };
    }

    private AlertDialog.OnClickListener getDialogClickListener(final Marker marker)
    {
        return new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                switch (i)
                {
                    case Dialog.BUTTON_POSITIVE:
                    {
                        String runnerKey=null;
                        //Viene ricercata la chiave associata al marker cliccato
                        //per poter inviare la richiesta al runner associato al marker
                        for(HashMap.Entry entry : nearbyRunners.entrySet())
                        {
                            if (marker.equals(entry.getValue()))
                            {
                                runnerKey = (String) entry.getKey();
                                break;
                            }
                        }
                        //Invio Richiesta
                        sendRequest(runnerKey);

                    }
                    break;
                    case Dialog.BUTTON_NEGATIVE:
                    {
                    }
                    break;
                }
            }
        };
    }

    private void registerRunnerForRequests()
    {
        //Registrazione presso il db che consente di gestire le richieste
        DatabaseReference consumerReference=liveRequestsDB.getReference(RunnersDatabases.LIVE_REQUEST_DB_ROOT);
        consumerReference=consumerReference.child(MainActivity.user.getNickname());
        //Registrazione listener che gestirà le richieste in arrivo
        consumerReference.addChildEventListener(getReceivedRequestListener());
    }

    private void sendRequest(String runner)
    {
        //Navigo verso il nodo a cui inviare la richiesta
        DatabaseReference consumerReference=liveRequestsDB.getReference(RunnersDatabases.LIVE_REQUEST_DB_ROOT);
        consumerReference=consumerReference.child(runner+"/"+MainActivity.user.getNickname());
        consumerReference.setValue(new Date());
        consumerReference=consumerReference.child(RunnersDatabases.LIVE_REQUEST_DB_ANSWER_NODE);
        consumerReference.addValueEventListener(getOnRequestAnsweredListener());
    }

    private ChildEventListener getReceivedRequestListener()
    {
        return new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                String key=dataSnapshot.getKey();
                RunnerDao runnerDao=new RunnerDaoImpl();
                Runner runner=runnerDao.getByNick(key);
                //Date da sostituire
                Log.i("SNAPSHOT",""+dataSnapshot.getValue());
                LiveRequest request=new LiveRequest(runner,new Date());
                inboxRequestsAdapter.add(request);
                inboxRequestsAdapter.notifyDataSetChanged();

                Toast.makeText(getContext(),"Richiesta ricevuta da "+key,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
    }

    private ValueEventListener getOnRequestAnsweredListener()
    {
        return new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue()!=null&&((String)dataSnapshot.getValue()).contains(RunnersDatabases.LIVE_REQEUEST_DB_REQUEST_ACCEPTED))
                    Toast.makeText(getContext(),"La richiesta è stata accettata",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
    }

    public void setInboxRequestsListView(ListView inboxRequestsListView)
    {
        this.inboxRequestsListView=inboxRequestsListView;
    }

    public void setInboxRequestsAdapter(LiveRequestsAdapter inboxRequestsAdapter)
    {
        this.inboxRequestsAdapter=inboxRequestsAdapter;
        inboxRequestsListView.setAdapter(inboxRequestsAdapter);
    }
}
