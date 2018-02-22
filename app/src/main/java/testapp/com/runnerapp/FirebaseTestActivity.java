package testapp.com.runnerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.GeoUser;
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


           List<Runner> runners = new PActiveRunDaoImpl().findRunnerByRun(ru.getId());
            saveRunFirebase(ru,runners);
         }

/*
        geofire.queryAtLocation(new GeoLocation(40.6960004,14.710742100000061),12).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                Log.i("onDataEntered",dataSnapshot.getKey());
                Long datestart = dataSnapshot.child("datestart").getValue(Long.class);
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
                        }
                    } // End while


                    if(!istrue){

                        // Inserisci gara all'interno della sezione partecipa

                    }
                } //End if

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

                Log.i("onDataExited",dataSnapshot.getKey());

                Long datestart = dataSnapshot.child("datestart").getValue(Long.class);
                if(datestart>=System.currentTimeMillis()){

                    //rimuovi gara dalla sezione partecipa


                } //End if


            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        }); */



    }


    public void saveRunFirebase(Run run, List<Runner> runners){

        databaserunners.child(String.valueOf(run.getId())).setValue(run);
        geofire.setLocation(String.valueOf(run.getId()), new GeoLocation(run.getMeetingPoint().latitude, run.getMeetingPoint().longitude));
        Map map = new HashMap();
        map.put("datestart",run.getStartDate().getTime());
        databaserunners.child(String.valueOf(run.getId())).updateChildren(map);

        DatabaseReference refrun = databaserunners.child(String.valueOf(run.getId())).child("participation");

        for(Runner runner: runners){
            refrun.child(String.valueOf(runner.getNickname())).setValue(runner.getNickname());
        }

    }

}
