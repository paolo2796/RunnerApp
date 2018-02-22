package testapp.com.runnerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import it.unisa.runnerapp.Dao.Implementation.FinishedRunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.FinishedRunDao;
import it.unisa.runnerapp.adapters.AcceptedRequestsAdapter;
import it.unisa.runnerapp.adapters.LiveRequestsAdapter;
import it.unisa.runnerapp.adapters.LiveRunListsAdapter;
import it.unisa.runnerapp.beans.FinishedRun;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.fragments.AcceptedRequestsListFragment;
import it.unisa.runnerapp.fragments.MapFragment;
import it.unisa.runnerapp.fragments.ReceivedRequestsListFragment;
import it.unisa.runnerapp.utils.RunnersDatabases;

public class LiveRunActivity extends CheckPermissionActivity
{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle aBarToggle;

    private MapFragment mapFragment;

    private int runCode;
    private int requestCode;

    public static final String LIVERUN_RUNCODE_KEY="RunCode";
    public static final String LIVERUN_REQCODE_KEY="RequestCode";

    public static Runner user=MainActivityPV.userlogged;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_run_panel);

        //Recupero codice corsa e request code dall'intent
        Intent intent=getIntent();
        if(intent!=null)
        {
            runCode=intent.getIntExtra(LIVERUN_RUNCODE_KEY,-1);
            requestCode=intent.getIntExtra(LIVERUN_REQCODE_KEY,-1);
        }

        //Recupero container fragment map
        FrameLayout frameLayout=findViewById(R.id.container);
        //Recupero delle text view con i dati della corsa
        TextView tvAvgSpeed=findViewById(R.id.avg_velocity);
        TextView tvBurnedCalories=findViewById(R.id.burned_cal);
        TextView tvTraveledDistance=findViewById(R.id.traveled_distance);
        //Recupero pannello a scorrimento con dati corsa
        SlidingUpPanelLayout slidingUpPanelLayout=findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelSlideListener(getPanelSlideListener());

        //Setting Up Toolbar
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Setting Up Finish Run Button
        ImageButton finishRunButton=findViewById(R.id.finishRunButton);
        finishRunButton.setOnClickListener(getFinishRunListener());

        //Setting Up DrawerLayout
        drawerLayout=(DrawerLayout) findViewById(R.id.sideNavRequest);
        aBarToggle=getActionBarToggle();
        aBarToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(aBarToggle);

        //Setting Up TabLayout inside ListView
        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs);
        ViewPager viewPager=(ViewPager)findViewById(R.id.pager);

        LiveRunListsAdapter liveRunListsAdapter=new LiveRunListsAdapter(getSupportFragmentManager());
        ReceivedRequestsListFragment receivedRequestFragment=liveRunListsAdapter.getReceivedRequestsFragment();
        AcceptedRequestsListFragment acceptedRequestsFragment=liveRunListsAdapter.getAcceptedRequestsFragment();

        viewPager.setAdapter(liveRunListsAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //Creazione adapters richieste in arrivo e richieste accettate
        AcceptedRequestsAdapter acceptedRequestsAdapter=new AcceptedRequestsAdapter(this,R.layout.nv_acceptedrequests_requestitem,new ArrayList<Runner>());
        LiveRequestsAdapter liveRequestsAdapter=new LiveRequestsAdapter(this,R.layout.nv_receivedrequests_requestitem,new ArrayList<LiveRequest>());
        //Setting Adapters ai fragment delle richieste in arrivo e delle richieste accettate
        liveRequestsAdapter.setAcceptedRequestsAdapter(acceptedRequestsAdapter);
        receivedRequestFragment.setRequestsAdapter(liveRequestsAdapter);
        acceptedRequestsFragment.setAcceptedRequestsAdapter(acceptedRequestsAdapter);

        //Aggiunta Fragment Mappa al layout
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        mapFragment=new MapFragment();
        ft.add(R.id.container,mapFragment);
        ft.commit();

        //Setting adapters richieste in arrivo e richieste accettate
        //al fragment map
        mapFragment.setInboxRequestsAdapter(liveRequestsAdapter);
        mapFragment.setAcceptedRequestsAdapter(acceptedRequestsAdapter);
        //Setting up delle text view che conterranno i dati della corsa
        mapFragment.setBurnedCaloriesTextView(tvBurnedCalories);
        mapFragment.setTraveledDistanceTextView(tvTraveledDistance);
        mapFragment.setAvgVelocityTextView(tvAvgSpeed);

        //Aggiunta FAB per il clear map necessario per la cancellazione
        //di un eventual percorso tracciato su di essa
        FloatingActionButton clearMapFAB=getClearMapFAB();
        frameLayout.addView(clearMapFAB);
        frameLayout.invalidate();
        //Setting up del FAB all'adapter che consente la visualizzazione
        //del percorso sul fragment map
        acceptedRequestsAdapter.setFloatingActionButton(clearMapFAB);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        aBarToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(aBarToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener getFinishRunListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("MAMMT","Cliccato");
                //Viene stoppato l'avvio del service
                mapFragment.stopBackgroundUpdates();
                //Viene cancellata la posizione live del runner dal db
                FirebaseDatabase locationsDB=mapFragment.getLocationDatabase();
                DatabaseReference runnerReference=locationsDB.getReference(RunnersDatabases.USER_LOCATIONS_DB_ROOT+"/"+user.getNickname());
                runnerReference.removeValue();
                //Viene cancellato il reference per le richieste live
                FirebaseDatabase liveRequestsDB=mapFragment.getLiveRequestsDatabase();
                DatabaseReference runnerRequestsReference=liveRequestsDB.getReference(RunnersDatabases.LIVE_REQUEST_DB_ROOT+"/"+user.getNickname());
                runnerRequestsReference.removeValue();
                //Viene azzerato il file shared preferences
                SharedPreferences sharedPreferences=getSharedPreferences(MapFragment.SP_ACCEPTED_REQUESTS_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.clear();
                editor.commit();
                //Memorizzazione dei dati nel db mysql
                FinishedRun finishedRun=new FinishedRun();
                Runner runner=new Runner();
                runner.setNickname(user.getNickname());
                finishedRun.setId(runCode);
                finishedRun.setRunner(runner);
                finishedRun.setAverageSpeed(mapFragment.getAvgVelocity());
                finishedRun.setBurnedCal(mapFragment.getBurnedCalories());
                finishedRun.setTraveledKm(mapFragment.getTraveledKilometers());
                FinishedRunDao finishedRunDao=new FinishedRunDaoImpl();
                finishedRunDao.createFinishedRun(finishedRun);
                //Settaggio del codice corsa chiusa
                Intent intent=new Intent();
                intent.putExtra(LIVERUN_RUNCODE_KEY,runCode);
                setResult(requestCode,intent);
                //Chiusura activity corrente
                finish();
            }
        };
    }

    private ActionBarDrawerToggle getActionBarToggle()
    {
        return new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };
    }

    private FloatingActionButton getClearMapFAB()
    {
        FloatingActionButton fab=new FloatingActionButton(this);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity= Gravity.CENTER|Gravity.RIGHT;
        lp.rightMargin=15;
        fab.setLayoutParams(lp);
        Drawable fabIcon=getResources().getDrawable(R.drawable.ic_close_black_24dp);
        fabIcon.setTint(getResources().getColor(R.color.background_list));
        fab.setImageDrawable(fabIcon);
        fab.setBackgroundColor(getResources().getColor(R.color.fab_show_path));
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_show_path)));
        fab.setVisibility(View.INVISIBLE);

        return fab;
    }

    private SlidingUpPanelLayout.PanelSlideListener getPanelSlideListener()
    {
        return new SlidingUpPanelLayout.PanelSlideListener()
        {
            @Override
            public void onPanelSlide(View panel, float slideOffset)
            {
            }

            @Override
            public void onPanelCollapsed(View panel)
            {
                //Visualizzazione icona apertura quando è chiuso
                ImageView icon=findViewById(R.id.sliderIcon);
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_40dp));
                icon.invalidate();
            }

            @Override
            public void onPanelExpanded(View panel)
            {
                //Visualizzazione icona chiusura quando è aperto
                ImageView icon=findViewById(R.id.sliderIcon);
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_40dp));
                icon.invalidate();
            }

            @Override
            public void onPanelAnchored(View panel)
            {
            }

            @Override
            public void onPanelHidden(View panel)
            {
            }
        };
    }
}
