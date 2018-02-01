package testapp.com.runnerapp;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;

public class MainActivity extends AppCompatActivity
{
    public static Runner user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        setContentView(R.layout.activity_main);

       /* FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Fragment mf=new MapFragment();
        ft.add(R.id.container,mf);
        ft.commit();
        user=new Runner("mavit","pass","Mauro","Vitale",null,null,70,200,(short)1); */


         /* Date datainizio = new Date();
        datainizio.setHours(20);
        datainizio.setMinutes(10);



        Runner runner = new RunnerDaoImpl().getByNick("paolo2796");

        ActiveRun activerun = new ActiveRun(new LatLng(40.6739591,9.186515999999983), datainizio,runner, 15.20, 7,10);
        new ActiveRunDaoImpl().createActiveRun(activerun);

        */


    }
}
