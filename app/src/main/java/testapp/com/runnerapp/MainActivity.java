package testapp.com.runnerapp;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.fragments.MapFragment;

public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle aBarToggle;

    public static Runner user=new Runner("paolo2796","pass","Mauro","Vitale",null,null,70,200,(short)1);;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        setContentView(R.layout.activity_main);

        /*
        setContentView(R.layout.live_run_panel);

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


         /* Date datainizio = new Date();
        datainizio.setHours(20);
        datainizio.setMinutes(10);
        Runner runner = new RunnerDaoImpl().getByNick("paolo2796");

        ActiveRun activerun = new ActiveRun(new LatLng(40.6739591,9.186515999999983), datainizio,runner, 15.20, 7,10);
        new ActiveRunDaoImpl().createActiveRun(activerun);
        */

        /*
        lw.addHeaderView(getLayoutInflater().inflate(R.layout.nv_liverequests_header,lw,false));
        mf.setInboxRequestsListView(lw);
        List<LiveRequest> lrs=new ArrayList<>();
        mf.setInboxRequestsAdapter(new LiveRequestsAdapter(this,R.layout.nv_liverequests_requestitem,lrs));*/
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
