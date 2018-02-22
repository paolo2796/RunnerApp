package it.unisa.runnerapp.utils;


import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import it.unisa.runnerapp.utils.Route;

public interface DirectionFinderListener {
    void clearMap();
    void onDirectionFinderSuccess(List<Route> route);
}