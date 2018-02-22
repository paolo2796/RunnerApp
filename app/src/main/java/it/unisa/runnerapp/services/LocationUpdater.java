package it.unisa.runnerapp.services;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import it.unisa.runnerapp.fragments.MapFragment;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.GeoUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;
import it.unisa.runnerapp.utils.ServiceBuffer;

public class LocationUpdater extends Service
{
    private LocationManager lManager;
    private GeoFire         gFire;
    private String          userKey;
    private int             timeInterval;
    private int             distanceInterval;

    private LocationListener locationListener;
    private Location         currentLocation;

    //Keys per il recupero dei dati passati dal chiamante
    public static final String USER_ID_KEY="UserKey";
    public static final String TIME_INTERVAL_KEY="TimeInterval";
    public static final String DISTANCE_INTERVAL_KEY="DistanceInterval";

    private static final int TIME_INTERVAL_DEFAULT=100;
    private static final int DISTANCE_INTERVAL_DEFAULT=1;

    public LocationUpdater(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        super.onStartCommand(intent,flags,startId);

        if(intent!=null)
        {
            userKey=intent.getStringExtra(USER_ID_KEY);
            timeInterval=intent.getIntExtra(TIME_INTERVAL_KEY,TIME_INTERVAL_DEFAULT);
            distanceInterval=intent.getIntExtra(DISTANCE_INTERVAL_KEY,DISTANCE_INTERVAL_DEFAULT);

            lManager=GeoUtils.getLocationManager(getApplicationContext());
            LocationProvider lProvider=GeoUtils.getBestProvider(lManager);
            locationListener=getLocationListener();
            GeoUtils.startLocationUpdates(lManager,lProvider.getName(),timeInterval,distanceInterval,locationListener);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase locationDatabase = ServiceBuffer.locationsDb;
        DatabaseReference dr=locationDatabase.getReference(RunnersDatabases.USER_LOCATIONS_DB_ROOT);
        gFire=new GeoFire(dr);
    }

    @Override
    public void onDestroy()
    {
        GeoUtils.stopLocationUpdates(lManager,locationListener);
        MapFragment.receiveLocation(currentLocation);
    }

    private LocationListener getLocationListener()
    {
        return new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                gFire.setLocation(userKey,new GeoLocation(location.getLatitude(),location.getLongitude()));
                currentLocation=location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };
    }
}
