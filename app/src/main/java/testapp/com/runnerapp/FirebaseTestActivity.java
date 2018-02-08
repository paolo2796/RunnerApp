package testapp.com.runnerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;

public class FirebaseTestActivity extends AppCompatActivity {

    private static final String MESSAGE_LOG="Messaggio";
    private FirebaseApp participationapp;
    private FirebaseDatabase participationdb;
    private DatabaseReference databaserunners;

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


        databaserunners.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Run newPost = dataSnapshot.getValue(Run.class);
                Log.i("Messaggio","id: " + newPost.getId());
                Log.i("Messaggio","date: " + newPost.getStartDate().toString());
                Log.i("Messaggio","Previous Post ID: " + prevChildKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.i("Messaggio","onchildchanged" );


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.i("Messaggio","onchildremoved" );

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.i("Messaggio","onchildmoved" );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i("Messaggio","oncancelled" );

            }
        });



        ActiveRun activerun = new ActiveRunDaoImpl().findByID(1);
        activerun.setStartDate(new java.util.Date(2018,02,8,22,38,00));
        activerun.getMaster().setBirthDate(new java.util.Date(1996,01,27,0,0,0));


      /*  Run run = new Run();
        run.setId(1);
        run.setStartDate(new Date());
        Runner runner = new Runner();
        runner.setName("paolo");
        runner.setSurname("vigorito");
        runner.setNickname("paolo2796");

        runner.setPassword("paolo");
        run.setMaster(runner);
        databaserunners.child(String.valueOf(run.getId())).setValue(run); */

      databaserunners.child(String.valueOf(activerun.getId())).setValue(activerun);

        LatLng latLngcustom = new LatLng(activerun.getMeetingPoint().latitude,activerun.getMeetingPoint().longitude);

        databaserunners.child(String.valueOf(activerun.getId())).setValue(latLngcustom);


        databaserunners.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    Run run = null;

                    for(DataSnapshot child: dataSnapshot.getChildren()){

                        run = child.getValue(Run.class);
                        Log.i("messaggio",run.getMaster().getNickname());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //   Log.i(MESSAGE_LOG,"BEFORE");


    }

}
