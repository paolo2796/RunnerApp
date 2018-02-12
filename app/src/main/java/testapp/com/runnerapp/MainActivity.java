package testapp.com.runnerapp;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import it.unisa.runnerapp.adapters.AcceptedRequestsAdapter;
import it.unisa.runnerapp.adapters.LiveRequestsAdapter;
import it.unisa.runnerapp.adapters.LiveRunListsAdapter;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.fragments.AcceptedRequestsListFragment;
import it.unisa.runnerapp.fragments.MapFragment;
import it.unisa.runnerapp.fragments.ReceivedRequestsListFragment;

public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle aBarToggle;

    public static Runner user=new Runner("mavit","pass","Mauro","Vitale",null,null,70,200,(short)1);;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //setContentView(R.layout.activity_main);

        setContentView(R.layout.live_run_panel);

        //tabs
        /*
        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs);
        ViewPager viewPager=(ViewPager)findViewById(R.id.pager);
        LiveRunListsAdapter liveRunListsAdapter=new LiveRunListsAdapter(getSupportFragmentManager());
        ReceivedRequestsListFragment receivedRequestFragment=liveRunListsAdapter.getReceivedRequestsFragment();
        AcceptedRequestsListFragment acceptedRequestsFragment=liveRunListsAdapter.getAcceptedRequestsFragment();

        viewPager.setAdapter(liveRunListsAdapter);
        tabLayout.setupWithViewPager(viewPager);


        ListView lw=(ListView)findViewById(R.id.receivedRequestsList);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout=(DrawerLayout) findViewById(R.id.sideNavRequest);
        aBarToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        aBarToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(aBarToggle);

        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        MapFragment mf=new MapFragment();
        ft.add(R.id.container,mf);
        ft.commit();*/


        //View listHeader=getLayoutInflater().inflate(R.layout.nv_liverequests_header,lw,false);
        //TextView notificationBadge=(TextView)listHeader.findViewById(R.id.badge_request_number);
        //lw.addHeaderView(listHeader);
        //mf.setInboxRequestsListView(lw);
        //List<LiveRequest> lrs=new ArrayList<>();AcceptedRequestsAdapter acceptedRequestsAdapter=new AcceptedRequestsAdapter(this,R.layout.nv_acceptedrequests_requestitem,new ArrayList<Runner>());
        /*
        AcceptedRequestsAdapter acceptedRequestsAdapter=new AcceptedRequestsAdapter(this,R.layout.nv_acceptedrequests_requestitem,new ArrayList<Runner>());
        LiveRequestsAdapter liveRequestsAdapter=new LiveRequestsAdapter(this,R.layout.nv_receivedrequests_requestitem,new ArrayList<LiveRequest>());

        liveRequestsAdapter.setAcceptedRequestsAdapter(acceptedRequestsAdapter);
        receivedRequestFragment.setRequestsAdapter(liveRequestsAdapter);
        acceptedRequestsFragment.setAcceptedRequestsAdapter(acceptedRequestsAdapter);

        mf.setInboxRequestsAdapter(liveRequestsAdapter);
        mf.setAcceptedRequestsAdapter(acceptedRequestsAdapter);
        //mf.setNotificationBadge(notificationBadge);*/

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

}
