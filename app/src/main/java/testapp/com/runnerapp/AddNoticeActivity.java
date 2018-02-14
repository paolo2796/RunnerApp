package testapp.com.runnerapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import it.unisa.runnerapp.customwidgets.CustomMap;
import it.unisa.runnerapp.fragments.MyAdsFragment;

public class AddNoticeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapview;
    private Button datebtn;
    private Button timebtn;
    private EditText estimatedkmet;
    private Button estimatedtimebtn;
    private Calendar mdateandtime = Calendar.getInstance();
    private LatLng waypoint;
    private TimePickerDialog dialogtime;
    private DatePickerDialog datepickerdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotice);


        datebtn = (Button) findViewById(R.id.date_btn);
        timebtn = (Button) findViewById(R.id.time_btn);
        estimatedkmet = (EditText) findViewById(R.id.kmestimated_et);
        estimatedtimebtn = (Button) findViewById(R.id.estimatedtime_btn);
        mapview = (MapView) findViewById(R.id.mapview);
        mapview.onCreate(null);
        mapview.getMapAsync(this);
        ;


      /* Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_successfully);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show(); */


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().title("Ti trovi qui").position(new LatLng(40.7289515,14.705428799999936)).draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.7289515,14.705428799999936), 13));
        googleMap.setOnMarkerDragListener(getOnMarkerDrag());

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
        datepickerdialog.show();
    }



    public void onTimeEstimatedClicked(View v){
        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mdateandtime.set(Calendar.MINUTE, minute);
                updateEstimatedTimeDisplay();
            }
        };

        dialogtime = new TimePickerDialog(this,R.style.DialogThemeDateTime, mTimeListener, mdateandtime.get(Calendar.HOUR_OF_DAY), mdateandtime.get(Calendar.MINUTE), true);
        dialogtime.show();

    }


    private void updateDateAndTimeDisplay() {
        datebtn.setText(DateUtils.formatDateTime(this, mdateandtime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE));
        timebtn.setText(DateUtils.formatDateTime(this,mdateandtime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME));
    }

    private void updateEstimatedTimeDisplay(){
        estimatedtimebtn.setText(DateUtils.formatDateTime(this,mdateandtime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME));
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
