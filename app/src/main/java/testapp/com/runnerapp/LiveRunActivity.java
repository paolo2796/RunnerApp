package testapp.com.runnerapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import it.unisa.runnerapp.adapters.AcceptedRequestsAdapter;
import it.unisa.runnerapp.adapters.LiveRequestsAdapter;
import it.unisa.runnerapp.adapters.LiveRunListsAdapter;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.fragments.AcceptedRequestsListFragment;
import it.unisa.runnerapp.fragments.MapFragment;
import it.unisa.runnerapp.fragments.ReceivedRequestsListFragment;

public class LiveRunActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle aBarToggle;

    private MapFragment mapFragment;

    public static Runner user=new Runner("paolo2796","pass","Mauro","Vitale",null,null,70,200,(short)1);;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        setContentView(R.layout.live_run_panel);

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
        lp.gravity= Gravity.BOTTOM|Gravity.RIGHT;
        lp.bottomMargin=20;
        lp.rightMargin=15;
        fab.setLayoutParams(lp);
        Drawable fabIcon=getResources().getDrawable(R.drawable.ic_close_black_24dp);
        fabIcon.setTint(getResources().getColor(R.color.background_list));
        fab.setImageDrawable(fabIcon);
        fab.setBackgroundColor(getResources().getColor(R.color.colorAccent));
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
