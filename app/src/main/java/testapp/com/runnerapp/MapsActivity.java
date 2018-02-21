package testapp.com.runnerapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.unisa.runnerapp.utils.DirectionFinderImpl;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DirectionFinderImpl directionfinder;
    private Marker amarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(40.6960004, 9.186515999999983);
        MarkerOptions markerOptions = new MarkerOptions().position(sydney).title("Marker Cava");
       // mMap.addMarker(markerOptions);

        //amarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Hello World"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        directionfinder = new DirectionFinderImpl(getApplication(),googleMap, R.drawable.ic_datestart_30dp,R.drawable.ic_destination_35dp);
        directionfinder.executeDraw(new LatLng(40.729631, 14.705216), new LatLng(40.743340, 14.682257));
    }

    @Override

    public void onBackPressed(){

         directionfinder.executeDraw(new LatLng(40.673944,14.770186200000012), new LatLng(40.7035379,14.708282000000054));

       // amarker.remove();
    }
}
