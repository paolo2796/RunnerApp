package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import testapp.com.runnerapp.R;

public class MarkerWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private Context                ctx;

    public final static String SEND_REQUEST_DEBUG_KEY="RequestTask";

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
    public View getInfoContents(final Marker marker)
    {
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.custom_marker_window,null);
        return v;
    }
}
