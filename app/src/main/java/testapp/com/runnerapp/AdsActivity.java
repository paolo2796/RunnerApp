package testapp.com.runnerapp;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.Dao.Interf.PActiveRunDao;
import it.unisa.runnerapp.adapters.AdActiveAdapter;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.utils.CheckUtils;

/**
 * Created by Paolo on 01/02/2018.
 */

public class AdsActivity extends AppCompatActivity{

    List<ActiveRun> runsactive;
    ListView listview;
    AdActiveAdapter arrayadapter;
    ActiveRunDao activerundao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        setContentView(R.layout.activity_ads);



        listview = (ListView) this.findViewById(R.id.listview);

        List<ActiveRun> runs = new ActiveRunDaoImpl().getActiveRunsWithin24h("data_inizio");

        arrayadapter = new AdActiveAdapter(this,R.layout.row_adactive,runs);

        listview.setAdapter(arrayadapter);


    }



    public void requestParticipation(View v){

        int tag = Integer.parseInt(v.getTag().toString());
        ActiveRun activeruncurren = (ActiveRun) arrayadapter.getItem(tag);
        Button participation = v.findViewById(R.id.participatebtn);

        View view = arrayadapter.getView(tag,(View) v.getParent(),null);
        Button cancelrun = (Button) view.findViewById(R.id.cancelbtn);
        cancelrun.setVisibility(View.VISIBLE);
        participation.setVisibility(View.GONE);
        new PActiveRunDaoImpl().createParticipationRun(activeruncurren.getId(),"paolo2796");

    }


    public void cancelParticipation(View v){

        int tag = Integer.parseInt(v.getTag().toString());
        Button cancelrun = v.findViewById(R.id.cancelbtn);
        View view = arrayadapter.getView(tag,(View) v.getParent(),null);
        Button participation = (Button) view.findViewById(R.id.participatebtn);
        cancelrun.setVisibility(View.GONE);
        participation.setVisibility(View.VISIBLE);
        ActiveRun activeruncurren = (ActiveRun) arrayadapter.getItem(tag);
        new PActiveRunDaoImpl().deleteParticipationRun(activeruncurren.getId(),"paolo2796");

    }

}


