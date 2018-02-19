package testapp.com.runnerapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unisa.runnerapp.Dao.Implementation.ActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.PActiveRunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunDaoImpl;
import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.customwidgets.CustomMap;
import it.unisa.runnerapp.fragments.MyAdsFragment;

public class AddNoticeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static String MESSAGE_LOG="Messaggio Add";
    private MapView mapview;
    private Button datebtn;
    private Button timebtn;
    private Button addrun;
    private EditText estimatedkmet;
    private Button estimatedtimebtn;
    private Calendar mdateandtime = Calendar.getInstance();
    private Calendar estimatedtim = Calendar.getInstance();
    private LatLng waypoint;
    private LatLng myposition;
    private TimePickerDialog dialogtime;
    private DatePickerDialog datepickerdialog;

    // DB Firebase
    private GeoFire geofire;


    //Location
    private LocationManager locationmanager;
    private LocationListener locationlistener;
    private GoogleMap googlemap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotice);


        datebtn = (Button) findViewById(R.id.date_btn);
        timebtn = (Button) findViewById(R.id.time_btn);
        addrun =  (Button) findViewById(R.id.addrun_btn);
        estimatedkmet = (EditText) findViewById(R.id.kmestimated_et);
        estimatedtimebtn = (Button) findViewById(R.id.estimatedtime_btn);
        mapview = (MapView) findViewById(R.id.mapview);
        mapview.onCreate(null);
        mapview.getMapAsync(this);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        double mylatitude = getIntent().getDoubleExtra("mylatitude",0);
        double mylongitude = getIntent().getDoubleExtra("mylongitude",0);
        if(mylatitude!=0 && mylongitude!=0){
            myposition = new LatLng(mylatitude,mylongitude);

        }

        else{

            locationmanager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationlistener = getLocationListener();
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationlistener);

        }






    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googlemap = googleMap;
        if(myposition!=null) {
            googleMap.addMarker(new MarkerOptions().title("Ti trovi qui").position(myposition).draggable(true));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myposition, 13));
            googleMap.setOnMarkerDragListener(getOnMarkerDrag());

        }
    }



    public void onClickAddRun(View v){
        if(checkField()){
            addrun.setEnabled(false);
            java.util.Date date = new java.util.Date(mdateandtime.getTimeInMillis());
            Runner runner = new RunnerDaoImpl().getByNick(MainActivityPV.userlogged.getNickname());
            int estimatedkm = Integer.parseInt(estimatedkmet.getText().toString());
            String[] stringarray = estimatedtimebtn.getText().toString().split(":");
            int estimatedhour = Integer.parseInt(stringarray[0]);
            int estimatedmin = Integer.parseInt(stringarray[1]);
            ActiveRun activeRun = new ActiveRun(waypoint,date,runner,estimatedkm,estimatedhour,estimatedmin);
            new ActiveRunDaoImpl().createActiveRun(activeRun);
            new PActiveRunDaoImpl().createParticipationRun(activeRun.getId(),runner.getNickname());

            this.saveRunFirebase((Run) activeRun);

            Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog_successfully);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();


            addrun.setEnabled(true);


        }
        else{

            Toast.makeText(this,"Alcuni campi non sono corretti!",Toast.LENGTH_LONG).show();
        }

    }


    private boolean checkField(){
        String regex = "^[0-9.]+";

        if(myposition!=null && !datebtn.getText().equals("") && !timebtn.getText().equals("") && !estimatedtimebtn.getText().equals("") && estimatedkmet.getText().toString().matches(regex)){
            return true;
        }
        return false;
    }


    public void onTimeClicked(View v) {

        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mdateandtime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mdateandtime.set(Calendar.MINUTE, minute);
                updateDateAndTimeDisplay();
                //Toast.makeText(AddNoticeActivity.this,"HO SETTATO L'ORA",Toast.LENGTH_LONG).show();
            }
        };

        dialogtime = new TimePickerDialog(this,R.style.DialogThemeDateTime, mTimeListener, mdateandtime.get(Calendar.HOUR_OF_DAY), mdateandtime.get(Calendar.MINUTE), true);
        dialogtime.show();
    }

    public void onDateClicked(View v) {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mdateandtime.set(Calendar.YEAR, year);
                mdateandtime.set(Calendar.MONTH, monthOfYear);
                mdateandtime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateAndTimeDisplay();
                //Toast.makeText(AddNoticeActivity.this,"HO SETTATO LA DATA",Toast.LENGTH_LONG).show();
            }
        };

        datepickerdialog = new DatePickerDialog(this,R.style.DialogThemeDateTime, mDateListener, mdateandtime.get(Calendar.YEAR), mdateandtime.get(Calendar.MONTH), mdateandtime.get(Calendar.DAY_OF_MONTH));
        datepickerdialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datepickerdialog.show();
    }



    public void onTimeEstimatedClicked(View v){
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


    private void updateDateAndTimeDisplay() {
        datebtn.setText(DateUtils.formatDateTime(this, mdateandtime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE));
        timebtn.setText(DateUtils.formatDateTime(this,mdateandtime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME));
    }

    private void updateEstimatedTimeDisplay(){
        estimatedtimebtn.setText(DateUtils.formatDateTime(this,estimatedtim.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME));
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

    private LocationListener getLocationListener(){

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myposition = new LatLng(location.getLatitude(),location.getLongitude());
                onMapReady(googlemap);
                locationmanager.removeUpdates(locationlistener);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }


    public void saveRunFirebase(Run run){

        MainActivityPV.databaseruns.child(String.valueOf(run.getId())).setValue(run);
        geofire = new GeoFire(MainActivityPV.databaseruns);
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
