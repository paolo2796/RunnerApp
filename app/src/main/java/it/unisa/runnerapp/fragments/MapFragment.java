package it.unisa.runnerapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.Dao.Interf.RunnerDao;
import it.unisa.runnerapp.adapters.AcceptedRequestsAdapter;
import it.unisa.runnerapp.adapters.LiveRequestsAdapter;
import it.unisa.runnerapp.adapters.MarkerWindowAdapter;
import it.unisa.runnerapp.beans.GeoUser;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.services.LocationUpdater;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.GeoUtils;
import it.unisa.runnerapp.utils.NotificationUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;
import testapp.com.runnerapp.LiveRunActivity;
import testapp.com.runnerapp.R;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback
{
    private Context          ctx;

    private GeoUser          gUser;

    private static LocationManager  lManager;
    private LocationProvider        lProvider;
    private Location                bestLocation;
    private static LocationListener locationListener;

    private Long lastLocationUpdateTime;

    private GoogleMap              gMap;
    private LatLng                 userPosition;
    private HashMap<String,Marker> nearbyRunners;

    private LiveRequestsAdapter     inboxRequestsAdapter;
    private AcceptedRequestsAdapter acceptedRequestsAdapter;

    private FirebaseDatabase  locationsDB;
    private FirebaseDatabase  liveRequestsDB;
    private DatabaseReference userLocationReference;

    private GeoFire               gFire;
    private GeoQueryEventListener nearbyRunnersListener;

    private Intent  locationUpdaterService;
    private boolean locationUpdaterServiceState;

    private TextView tvBurnedCalories;
    private TextView tvTraveledDistance;
    private TextView tvAvgSpeed;

    private float  traveled_distance;
    private double burned_calories;
    private double avg_velocity;
    private int    avg_velocity_counter;

    //Array icone marker
    private static final int[] icons={R.drawable.ic_baseball,R.drawable.ic_basketball,R.drawable.ic_soccer,R.drawable.ic_tennis};

    private static final double RUNNERS_RESEARCH_RADIUS=0.8;

    private static final int   TIME_UPDATES=650;
    private static final int   DISTANCE_UPDATES=1;
    private static final float ZOOM_CAMERA=19.0f;

    private static final String ACCEPTED_REQUESTS_KEY="AcceptedUser";
    private static final String LOCATION_DEBUG_KEY="Location Update";
    public static final String  SP_ACCEPTED_REQUESTS_NAME="accepted_requests";

    private static final String VELOCITY_UNIT="m/s";
    private static final String CALORIES_UNIT="kCal";
    private static final String DISTANCE_UNIT="m";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ctx = getContext();
        //Inizializzazione mappa utenti nelle vicinanze
        nearbyRunners=new HashMap<>();
        //Inizializzazione location manager
        lManager = GeoUtils.getLocationManager(ctx);
        lProvider = GeoUtils.getBestProvider(lManager);

        //Connessione al db firebase per le posizioni
        FirebaseApp locationsApp=FirebaseUtils.getFirebaseApp(getContext(),
                RunnersDatabases.USER_LOCATIONS_APP_ID,
                RunnersDatabases.USER_LOCATIONS_API_KEY,
                RunnersDatabases.USER_LOCATIONS_DB_URL,
                RunnersDatabases.USER_LOCATIONS_DB_NAME);
        locationsDB=FirebaseUtils.connectToDatabase(locationsApp);
        //Connessione al db per le richieste in live
        FirebaseApp liveRequestsApp=FirebaseUtils.getFirebaseApp(getContext(),
                RunnersDatabases.LIVE_REQUEST_APP_ID,
                RunnersDatabases.LIVE_REQUEST_API_KEY,
                RunnersDatabases.LIVE_REQUEST_DB_URL,
                RunnersDatabases.LIVE_REQUEST_DB_NAME);
        //Connessione al db Firebase per le richieste in live
        liveRequestsDB=FirebaseUtils.connectToDatabase(liveRequestsApp);

        //Inizializzazione Adapter Richieste in Arrivo
        inboxRequestsAdapter.setDatabase(liveRequestsDB);
        inboxRequestsAdapter.setUser(LiveRunActivity.user.getNickname());
        inboxRequestsAdapter.setLocationManager(lManager);
        inboxRequestsAdapter.setNearbyRunners(nearbyRunners);
        //Iniazializzazione Adapter Richieste Accettate
        acceptedRequestsAdapter.setUser(LiveRunActivity.user.getNickname());
        acceptedRequestsAdapter.setLocationManager(lManager);

        //Inizializzazione Firebase per la ricezione delle richieste
        registerRunnerForRequests();
        //Recupero richieste accettate
        retrieveAcceptedRequests();

        //Inizializzazione sliding panel
        //Istante di tempo in cui è stato ricevuto il primo aggiornamento
        lastLocationUpdateTime= System.currentTimeMillis();
        //Inizializzazione contatore per il n.campionamenti
        avg_velocity_counter=0;
        //Attivazione del service
        locationUpdaterServiceState=true;

        tvAvgSpeed.setText("0 "+VELOCITY_UNIT);
        tvTraveledDistance.setText("0 "+DISTANCE_UNIT);
        tvBurnedCalories.setText("0 "+CALORIES_UNIT);
    }

    @Override
    public void onResume()
    {
        try
        {
            if(locationListener==null)
                locationListener = getLocationListener();
            //Richiesta aggiornamenti di posizione
            GeoUtils.startLocationUpdates(lManager, LocationManager.GPS_PROVIDER, TIME_UPDATES,DISTANCE_UPDATES, locationListener);

            if(locationUpdaterService!=null)
            {
                //Il service viene arrestato
                ctx.stopService(locationUpdaterService);
                locationUpdaterService=null;
                //Ultima posizione disponibile
                Location location=lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        catch (SecurityException ex)
        {
            //Permessi non approvati dall'utente
            //reagire di conseguenza
        }
        finally
        {
            super.onResume();
        }
    }

    @Override
    public void onPause()
    {
        //Stop agli aggiornamenti in foreground
        GeoUtils.stopLocationUpdates(lManager,locationListener);
        //Se è richiesta l'attivazione del service
        if(locationUpdaterServiceState)
        {
            //Creazione intent da lanciare
            locationUpdaterService=new Intent(ctx, LocationUpdater.class);
            //Aggiunta informazioni necessarie quali nickname dell'utente
            //e gli intervalli di tempo e distanza con cui richiedere gli
            //aggiornamenti
            locationUpdaterService.putExtra(LocationUpdater.USER_ID_KEY, LiveRunActivity.user.getNickname());
            locationUpdaterService.putExtra(LocationUpdater.TIME_INTERVAL_KEY,TIME_UPDATES);
            locationUpdaterService.putExtra(LocationUpdater.DISTANCE_INTERVAL_KEY,DISTANCE_UPDATES);
            //Avvio del service
            ctx.startService(locationUpdaterService);
        }
        super.onPause();
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
            //Inizializzazione mappa per l'adapter
            acceptedRequestsAdapter.setGoogleMap(googleMap);

            gMap=googleMap;
            gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ctx,R.raw.map_style));
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
                    if(bestLocation!=null)
                    {
                        avg_velocity_counter++;
                        //Distanza percorsa in metri
                        float distance=bestLocation.distanceTo(location);
                        long currentTime=System.currentTimeMillis();
                        //Tempo impiegato per raggiungere la nuova locazione
                        long spentTime=currentTime-lastLocationUpdateTime;
                        //Tempo impegato in secondi
                        spentTime=spentTime/1000;
                        //Velocità in metri al secondo
                        double current_velocity= (double)distance/spentTime;
                        avg_velocity=((avg_velocity*(avg_velocity_counter-1))+current_velocity)/avg_velocity_counter;
                        traveled_distance+=distance;
                        //Calcolo delle calorie bruciate,della velocità e della distanza percorsa
                        burned_calories= LiveRunActivity.user.getWeight()*(distance/1000);

                        Log.i("DISTANCE",""+distance+" metri");
                        Log.i("TIME",""+spentTime+" sec");
                        Log.i("VELOCITY",""+avg_velocity+" m/s");
                        lastLocationUpdateTime=System.currentTimeMillis();

                        //Aggiornamento valori visualizzati
                        DecimalFormat formatter=new DecimalFormat("##.##");
                        tvAvgSpeed.setText(formatter.format(avg_velocity)+" "+VELOCITY_UNIT);
                        tvTraveledDistance.setText(formatter.format(traveled_distance)+" "+DISTANCE_UNIT);
                        tvBurnedCalories.setText(formatter.format(burned_calories)+" "+CALORIES_UNIT);
                    }
                    else
                        lastLocationUpdateTime=System.currentTimeMillis();

                    bestLocation=location;
                    //Se la mappa non è stata inizializzata vengono creati i dati per farlo
                    //e viene ottenuto il riferimento al nodo figlio che rappresenta l'utente
                    //nella base di dati di firebase
                    if(gUser==null)
                    {
                        gUser=new GeoUser(LiveRunActivity.user.getNickname());
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
                    Marker marker=gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.latitude,location.longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(CheckUtils.getBitmapFromVectorDrawable(getContext(),getRandomIcon()))));
                    marker.setTag(key);
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
                    marker.setPosition(new LatLng(location.latitude,location.longitude));
                    marker.setTag(key);
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
        consumerReference=consumerReference.child(LiveRunActivity.user.getNickname());
        //Registrazione listener che gestirà le richieste in arrivo
        consumerReference.addChildEventListener(getReceivedRequestListener());
    }

    private void sendRequest(String runner)
    {
        //Navigo verso il nodo a cui inviare la richiesta
        DatabaseReference consumerReference=liveRequestsDB.getReference(RunnersDatabases.LIVE_REQUEST_DB_ROOT);
        consumerReference=consumerReference.child(runner+"/"+ LiveRunActivity.user.getNickname());
        consumerReference.setValue(ServerValue.TIMESTAMP);
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
                //Recupero dati del sender
                RunnerDao runnerDao=new RunnerDaoImpl();
                Runner runner=runnerDao.getByNick(key);

                //Arrivo di una nuova richiesta
                try
                {
                    //Timestamp in cui è stata effettuata la richiesta
                    long timeInMills=(Long)dataSnapshot.getValue();
                    //Impostazione del fusorario locale
                    Calendar calendar=Calendar.getInstance(TimeZone.getDefault());
                    calendar.setTimeInMillis(timeInMills);
                    //Creazione della richiesta live sopraggiunta
                    //specificando sender e data/ora richiesta
                    LiveRequest request=new LiveRequest(runner,calendar.getTime());
                    //aggiunta alla lista delle richieste
                    inboxRequestsAdapter.add(request);
                    inboxRequestsAdapter.notifyDataSetChanged();

                    //Aggiornamento numero notifica
                    NotificationUtils.sendNotification(getContext(),"Be fast!",key+" ti ha inviato una richiesta!",R.drawable.ic_request_notification);
                }
                //Richiesta che è stata precedentemente accettata
                catch (ClassCastException ex)
                {
                    HashMap map=(HashMap)dataSnapshot.getValue();
                    Log.i("ACCETTATA",""+map);
                    DatabaseReference destRef=dataSnapshot.getRef();
                    Log.i("SNAPX",destRef.getKey());
                }
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
                //In questo caso la richiesta è stata accettata
                if(dataSnapshot.getValue()!=null&&((String)dataSnapshot.getValue()).contains(RunnersDatabases.LIVE_REQEUEST_DB_REQUEST_ACCEPTED))
                {
                    DatabaseReference destRef=dataSnapshot.getRef();
                    //Recupero dati dell'accettante
                    RunnerDao runnerDao=new RunnerDaoImpl();
                    Runner runner=runnerDao.getByNick(destRef.getParent().getParent().getKey());
                    Log.d("Chiave",destRef.getParent().getParent().getKey());
                    //Aggiunta richiesta accettata alla lista delle richieste accettate
                    //è recipient in quanto accettatario della richiesta
                    runner.isRecipient(true);
                    acceptedRequestsAdapter.add(runner);
                    acceptedRequestsAdapter.notifyDataSetChanged();
                    //Memorizzazione dell'accettante
                    SharedPreferences sharedPreferences=ctx.getSharedPreferences(SP_ACCEPTED_REQUESTS_NAME,Context.MODE_PRIVATE);
                    Set<String> users=sharedPreferences.getStringSet(ACCEPTED_REQUESTS_KEY,new HashSet<String>());
                    users.add(runner.getNickname());
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.clear();
                    editor.putStringSet(ACCEPTED_REQUESTS_KEY,users);
                    editor.commit();
                    //Invio della notifica
                    NotificationUtils.sendNotification(getContext(),"Keep in movement",runner.getNickname()+" ha accettato la tua richiesta!",R.drawable.ic_runner_notification);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        };
    }

    private int getRandomIcon()
    {
        Random random=new Random();
        return icons[random.nextInt(icons.length)];
    }

    public void setInboxRequestsAdapter(LiveRequestsAdapter inboxRequestsAdapter)
    {
        this.inboxRequestsAdapter=inboxRequestsAdapter;
    }

    public void setAcceptedRequestsAdapter(AcceptedRequestsAdapter acceptedRequestsAdapter)
    {
        this.acceptedRequestsAdapter=acceptedRequestsAdapter;
    }

    public void setAvgVelocityTextView(TextView tvAvgSpeed)
    {
        this.tvAvgSpeed=tvAvgSpeed;
    }

    public void setBurnedCaloriesTextView(TextView tvBurnedCalories)
    {
        this.tvBurnedCalories=tvBurnedCalories;
    }

    public void setTraveledDistanceTextView(TextView tvTraveledDistance)
    {
        this.tvTraveledDistance=tvTraveledDistance;
    }

    public void onSavedInstanceState(Bundle savedInstanceState)
    {
        //gUser
        //nearbyRunners
        //liveRequestAdapter
        //Firebases
        //GFire
    }

    public static void receiveLocation(Location location)
    {
        if(location!=null&&locationListener!=null)
        {
            locationListener.onLocationChanged(location);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

            try
            {
                lManager.requestSingleUpdate(criteria,locationListener,null);
            }
            catch (SecurityException ex)
            {
            }
        }
    }

    public void retrieveAcceptedRequests()
    {
        //Posizionamento sul nodo associato all'utente
        DatabaseReference dr=liveRequestsDB.getReference(RunnersDatabases.LIVE_REQUEST_DB_ROOT+"/"+ LiveRunActivity.user.getNickname());
        //Recupero dei figli
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //Scorrimento dei figli
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    final DatabaseReference childRef=ds.getRef().child("answer");
                    if(childRef!=null)
                    {
                        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                String value=(String)dataSnapshot.getValue();
                                if (value!=null&&value.equals(RunnersDatabases.LIVE_REQEUEST_DB_REQUEST_ACCEPTED))
                                {
                                    RunnerDao runnerDao=new RunnerDaoImpl();
                                    Runner runner=runnerDao.getByNick(childRef.getParent().getKey());
                                    //non recipient in quanto è colui che ha inviato la richiesta
                                    runner.isRecipient(false);
                                    acceptedRequestsAdapter.add(runner);
                                    acceptedRequestsAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        };

        dr.addListenerForSingleValueEvent(eventListener);

        //Recupero richieste inviate che sono state accettate
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(SP_ACCEPTED_REQUESTS_NAME,Context.MODE_PRIVATE);
        //Recupero degli utenti accettanti nella corsa (o sessione) corrente
        Set<String> users=sharedPreferences.getStringSet(ACCEPTED_REQUESTS_KEY,null);
        if(users!=null)
        {
            RunnerDao runnerDao=new RunnerDaoImpl();

            for(String user:users)
            {
                Runner runner=runnerDao.getByNick(user);
                //recipient in quanto accettatario della richiesta
                runner.isRecipient(true);
                acceptedRequestsAdapter.add(runner);
            }

            acceptedRequestsAdapter.notifyDataSetChanged();
        }
    }

    public void stopBackgroundUpdates()
    {
        this.locationUpdaterServiceState=false;
    }

    public FirebaseDatabase getLocationDatabase()
    {
        return locationsDB;
    }

    public FirebaseDatabase getLiveRequestsDatabase()
    {
        return liveRequestsDB;
    }

    public float getTraveledKilometers()
    {
        return traveled_distance;
    }

    public double getBurnedCalories()
    {
        return burned_calories;
    }

    public double getAvgVelocity()
    {
        return avg_velocity;
    }
}
