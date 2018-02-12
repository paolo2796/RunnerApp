package testapp.com.runnerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.DirectionFinderImpl;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;

public class FirebaseTestActivity extends AppCompatActivity{

    private static final String MESSAGE_LOG="Messaggio";
    private FirebaseApp participationapp;
    private FirebaseDatabase participationdb;
    private DatabaseReference databaserunners;
    private GeoFire geofire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);




       participationapp = FirebaseUtils.getFirebaseApp(this,
                RunnersDatabases.LIVE_REQUEST_APP_ID,
                RunnersDatabases.LIVE_REQUEST_API_KEY,
                RunnersDatabases.PARTICIPATION_DB_URL,
                RunnersDatabases.PARTICIPATION_DB_NAME);

        participationdb = FirebaseUtils.connectToDatabase(participationapp);
        databaserunners = participationdb.getReference("Runs");

        geofire = new GeoFire(databaserunners);



         List<Run> run = new RunDaoImpl().getAllRuns();
        for(Run ru: run){
             saveRunFirebase(ru);
         }


        geofire.queryAtLocation(new GeoLocation(40.6960004,14.710742100000061),12).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                for(DataSnapshot data: dataSnapshot.getChildren()){

                    String messagebody=data.getValue().toString();

                        Log.i("Messaggio",messagebody);
                }


            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

                Log.i("Messaggio exit",String.valueOf(dataSnapshot.getKey()));

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.i("Messaggio moved",String.valueOf(dataSnapshot.getKey()));

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.i("Messaggio changed",String.valueOf(dataSnapshot.getKey()));

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



    }


    public void saveRunFirebase(Run run){

        databaserunners.child(String.valueOf(run.getId())).setValue(run);
        geofire.setLocation(String.valueOf(run.getId()), new GeoLocation(run.getMeetingPoint().latitude, run.getMeetingPoint().longitude));
        Map map = new HashMap();
        map.put("datestart",run.getStartDate().getTime());
        databaserunners.child(String.valueOf(run.getId())).updateChildren(map);

    }

}
