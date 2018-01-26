package testapp.com.runnerapp;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.fragments.MapFragment;

public class MainActivity extends AppCompatActivity
{
    public static Runner user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        setContentView(R.layout.activity_main);
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Fragment mf=new MapFragment();
        ft.add(R.id.container,mf);
        ft.commit();

        user=new Runner("mavit","pass","Mauro","Vitale",null,null,70,200,(short)1);
    }
}
