package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.Dao.Interf.RunnerDao;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
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

        String key=(String)marker.getTag();
        RunnerDao runnerDao=new RunnerDaoImpl();
        Runner runner=runnerDao.getByNick(key);

        TextView tvNames=(TextView)v.findViewById(R.id.names);
        TextView tvPersonalInfo=(TextView)v.findViewById(R.id.personalInfo);
        TextView tvRunInfo=(TextView)v.findViewById(R.id.runInfo);

        tvNames.setText(runner.getName()+" "+runner.getSurname()+","+runner.getNickname());
        tvPersonalInfo.setText(CheckUtils.getAge(runner.getBirthDare())+" anni, Livello"+runner.getLevel());
        tvRunInfo.setText(runner.getTraveledKilometers()+" km");

        return v;
    }
}
