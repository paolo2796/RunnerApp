package testapp.com.runnerapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;

/**
 * Created by Paolo on 20/02/2018.
 */

public class EditRunActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static String MESSAGE_LOG = "Messaggio EditRunAc";

    //Component View
    TextView datestart_tw;
    TextView starthour_tw;
    EditText estimatedkmet;
    Button estimatedtimebtn;
    MapView mapview;
    Button edirun;


    TimePickerDialog dialogtime;
    GoogleMap googlemap;
    Calendar estimatedtim;
    LatLng waypoint;


    static Runner userlogged = MainActivityPV.userlogged;
    ActiveRun activerun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editrun);
        estimatedtim = Calendar.getInstance();

        activerun = new ActiveRunDaoImpl().findByID(getIntent().getIntExtra("codrun", -1));

        //Set View
        datestart_tw = (TextView) findViewById(R.id.datestart_tw);
        starthour_tw = (TextView) findViewById(R.id.starthour_tw);
        estimatedkmet = (EditText) findViewById(R.id.kmestimated_et);
        estimatedtimebtn = (Button) findViewById(R.id.estimatedtime_btn);
        edirun =  (Button) findViewById(R.id.editrun_btn);
        mapview = (MapView) findViewById(R.id.mapview);


        //Set Values
        datestart_tw.setText(CheckUtils.convertDateToStringFormat(activerun.getStartDate()));
        starthour_tw.setText(CheckUtils.convertHMToStringFormat(activerun.getStartDate()));
        estimatedkmet.setText(String.valueOf(activerun.getEstimatedKm()));
        estimatedtimebtn.setText(String.valueOf(activerun.getEstimatedHours()+ ":" + activerun.getEstimatedMinutes()));
        waypoint = activerun.getMeetingPoint();


        //Set Listeners
        mapview.onCreate(null);
        mapview.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

            this.googlemap = googleMap;
            googleMap.addMarker(new MarkerOptions().title("Punto Incontro").position(waypoint).draggable(true));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waypoint, 13));
            googleMap.setOnMarkerDragListener(getOnMarkerDrag());

    }


    private GoogleMap.OnMarkerDragListener getOnMarkerDrag(){

        return new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                waypoint = marker.getPosition();

            }
        };

    }


    public void onClickEditRun(View v){


        if(estimatedkmet.getText().toString().length()>0){

            activerun.setMeetingPoint(waypoint);
            activerun.setEstimatedKm(Double.parseDouble(estimatedkmet.getEditableText().toString()));
            String[] stringarray = estimatedtimebtn.getText().toString().split(":");
            int estimatedhour = Integer.parseInt(stringarray[0]);
            int estimatedmin = Integer.parseInt(stringarray[1]);
            activerun.setEstimatedHours(estimatedhour);
            activerun.setEstimatedMinutes(estimatedmin);
            new RunDaoImpl().updateRun(activerun);
            new ActiveRunDaoImpl().updateActiveRun(activerun);
            updateRunFirebase(activerun);


            Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog_successfully);
            ((TextView)dialog.findViewById(R.id.successtw)).setText("Modifica avvenuta con successo!");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

        }


    }


    public void onTimeEstimatedClicked(View v) {

        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                estimatedtim.set(Calendar.HOUR_OF_DAY, hourOfDay);
                estimatedtim.set(Calendar.MINUTE, minute);
                updateEstimatedTimeDisplay();
            }
        };

        dialogtime = new TimePickerDialog(this,R.style.DialogThemeDateTime, mTimeListener, estimatedtim.get(Calendar.HOUR_OF_DAY), estimatedtim.get(Calendar.MINUTE), true);
        dialogtime.show();
    }

    private void updateEstimatedTimeDisplay(){
        estimatedtimebtn.setText(DateUtils.formatDateTime(this,estimatedtim.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME));
    }

    private void updateRunFirebase(Run run){

        MainActivityPV.databaseruns.child(String.valueOf(run.getId())).setValue(run);
        GeoFire geofire = new GeoFire(MainActivityPV.databaseruns);
        geofire.setLocation(String.valueOf(run.getId()), new GeoLocation(run.getMeetingPoint().latitude, run.getMeetingPoint().longitude));
        Map map = new HashMap();
        map.put("datestart",run.getStartDate().getTime());
        MainActivityPV.databaseruns.child(String.valueOf(run.getId())).updateChildren(map);
        DatabaseReference refrun = MainActivityPV.databaseruns.child(String.valueOf(run.getId())).child("participation");
        refrun.child(String.valueOf(run.getMaster().getNickname())).setValue(run.getMaster().getNickname());
    }


    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();

    }


    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapview.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

}
