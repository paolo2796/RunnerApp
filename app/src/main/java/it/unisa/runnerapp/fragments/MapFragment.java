package it.unisa.runnerapp.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import it.unisa.runnerapp.beans.GeoUser;
import it.unisa.runnerapp.utils.GeoUtils;
import testapp.com.runnerapp.MainActivity;


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

    private FirebaseDatabase  locationsDB;
    private DatabaseReference userLocationReference;

    private GeoFire               gFire;
    private GeoQueryEventListener nearbyRunnersListener;

    private static final double RUNNERS_RESEARCH_RADIUS=0.8;

    private static final int   TIME_UPDATES=650;
    private static final int   DISTANCE_UPDATES=1;
    private static final float ZOOM_CAMERA=19.0f;

    private static final String ROOT_LOCATIONS="runners";
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
                        userLocationReference=locationsDB.getReference(ROOT_LOCATIONS);
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
                if(!key.equals(gUser.getNickname()))
                {
                    Marker marker=gMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude,location.longitude)));
                    nearbyRunners.put(key,marker);
                }
            }

            @Override
            public void onKeyExited(String key)
            {
                Marker marker=nearbyRunners.get(key);
                marker.remove();
                nearbyRunners.remove(key);
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
}
