package it.unisa.runnerapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.fragments.AdActiveDetailFragment;
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
    boolean iswaypoint;

    public DirectionFinderImpl(Context cx, GoogleMap googleMap, int iconorigin, int icondestination, boolean iswaypoint){
        this.mMap = googleMap;
        this.cx = cx;
        this.iconorigin = iconorigin;
        this.icondestination = icondestination;
        this.iswaypoint = iswaypoint;

    }

    public void execute(LatLng origin, LatLng destination){

        try {

            new DirectionFinder(this,origin,destination).execute();

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

    }

    // Implementazione metodi DirectionFinderListener

    @Override
    public void onDirectionFinderStart() {


        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }


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

            if(iswaypoint){
                int med = route.points.size()/2;
                //Log.i("Messaggio size",String.valueOf(route.points.size()));
                //Log.i("Messaggio ic",String.valueOf(med));
                LatLng puntocentrale = new LatLng(route.points.get(med).latitude,route.points.get(med).longitude);
                //Log.i("Messaggio punto central",String.valueOf("Latitudine: " + route.points.get(med).latitude + "- Longitudine:" + route.points.get(med).longitude));
                mMap.addMarker(new MarkerOptions().title("Punto Incontro").position(puntocentrale));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

}
