package testapp.com.runnerapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.unisa.runnerapp.utils.ConnectionUtil;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;

/**
 * Created by Paolo on 10/02/2018.
 */


public class CheckPermissionActivity extends AppCompatActivity {

    private LocationManager locationmanager;

    // DB Firebase
    public static FirebaseApp participationapp;
    public static FirebaseDatabase participationdb;
    public  static DatabaseReference databaserunners;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationmanager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        initRunsFireBase();
    }

    @Override
    public void onResume(){
        super.onResume();
        netCheckIn();
        checkGPS();
    }

    // Ulteriore controllo quando l'utente apre la barra delle notifiche
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            netCheckIn();
            checkGPS();
        }
    }

    public boolean netCheckIn(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo()==null){
          startActivity(new Intent(this,ErrorConnectionActivity.class));
          return false;
        }
        return true;
    }

    public boolean checkGPS(){

        checkManifestPermission();
        if(!locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            Intent gpsoptionintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsoptionintent);

            return false;
        }

        return true;

    }

    public void checkManifestPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    public LocationManager getLocationmanager(){return locationmanager;}


    public void initRunsFireBase() {

        if(participationapp==null) {
            participationapp = FirebaseUtils.getFirebaseApp(this, RunnersDatabases.LIVE_REQUEST_APP_ID, RunnersDatabases.LIVE_REQUEST_API_KEY, RunnersDatabases.PARTICIPATION_DB_URL, RunnersDatabases.PARTICIPATION_DB_NAME);
            participationdb = FirebaseUtils.connectToDatabase(participationapp);
        }

    }


}
