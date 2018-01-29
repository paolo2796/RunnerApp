package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import testapp.com.runnerapp.R;

public class MarkerWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private Context ctx;

    public MarkerWindowAdapter(Context ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.custom_marker_window,null);
        return v;
    }
}
