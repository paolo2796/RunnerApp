package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.Dao.Interf.RunnerDao;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.LevelMapper;
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
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.infowindow_nearby_runners,null);

        String key=(String)marker.getTag();

        Log.i("RUNNER",key);
        RunnerDao runnerDao=new RunnerDaoImpl();
        Runner runner=runnerDao.getByNick(key);
        Log.i("RUNNER",runner.getName());
        TextView tvNames=(TextView)v.findViewById(R.id.names);
        TextView tvPersonalInfo=(TextView)v.findViewById(R.id.personalInfo);
        TextView tvRunInfo=(TextView)v.findViewById(R.id.runInfo);

        tvNames.setText(runner.getName()+" "+runner.getSurname()+","+runner.getNickname());
        tvPersonalInfo.setText(CheckUtils.getAge(runner.getBirthDare())+" anni, Livello "+ LevelMapper.getLevelName(runner.getLevel()));
        tvRunInfo.setText(runner.getTraveledKilometers()+" km");

        return v;
    }

    @Override
    public View getInfoContents(final Marker marker)
    {
        return null;
    }
}
