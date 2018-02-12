package it.unisa.runnerapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 08/02/2018.
 */

public class DirectionFinderImpl implements DirectionFinderListener {

    GoogleMap mMap = null;
    List<Polyline> polylinePaths = new ArrayList<>();
    Context cx;
    int iconorigin;
    int icondestination;
    private LatLng waypoint;


    public DirectionFinderImpl(Context cx, GoogleMap googleMap, int iconorigin, int icondestination){
        this.mMap = googleMap;
        this.cx = cx;
        this.iconorigin = iconorigin;
        this.icondestination = icondestination;

    }


    public DirectionFinderImpl(){};

    public LatLng execute(LatLng origin, LatLng destination){
        LatLng puntocentrale = null;

                List<Route> routes = null;
                try {
                    routes = new DirectionFinder(origin, destination).execute();
                    int med = routes.get(0).points.size()/2;
                    return puntocentrale = new LatLng(routes.get(0).points.get(med).latitude,routes.get(0).points.get(med).longitude);

                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                int med = routes.get(0).points.size() / 2;
                return puntocentrale = new LatLng(routes.get(0).points.get(med).latitude, routes.get(0).points.get(med).longitude);



    }

    public void executeDraw(LatLng origin, LatLng destination){


        try {
            new DirectionFinder(this,origin,destination).executeDraw();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    // Implementazione metodi DirectionFinderListener


    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        polylinePaths = new ArrayList<>();
        for (Route route : routes) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));
            Bitmap bitmapicon =  CheckUtils.getBitmapFromVectorDrawable(cx,iconorigin);
            MarkerOptions originoptionmarker= new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmapicon)).title(route.startAddress).position(route.startLocation);
            mMap.addMarker(originoptionmarker);

            bitmapicon =  CheckUtils.getBitmapFromVectorDrawable(cx,icondestination);
            MarkerOptions destinationoptionmarker= new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmapicon)).title(route.endAddress).position(route.endLocation);
            mMap.addMarker(destinationoptionmarker);
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(cx.getResources().getColor(R.color.tempv_celestial)).
                    width(10);
            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
    }

    @Override
    public void clearMap(){

        if(polylinePaths!=null) {
            for (Polyline pol : polylinePaths) {
                pol.remove();
            }
        }

        if(mMap!=null){
            mMap.clear();

        }
    }


}
