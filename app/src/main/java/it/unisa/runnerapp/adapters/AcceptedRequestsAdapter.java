package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unisa.runnerapp.Dao.Implementation.Request_LiveDaoImpl;
import it.unisa.runnerapp.Dao.Interf.Request_LiveDao;
import it.unisa.runnerapp.beans.RequestLive;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.DirectionFinderImpl;
import it.unisa.runnerapp.utils.LevelMapper;
import testapp.com.runnerapp.R;

public class AcceptedRequestsAdapter extends ArrayAdapter<Runner>
{
    private String user;

    private int                  resId;
    private LayoutInflater       inflater;
    private LocationManager      lManager;
    private GoogleMap            gMap;
    private DirectionFinderImpl  directionFinder;
    private FloatingActionButton clearButton;

    public AcceptedRequestsAdapter(Context ctx, int resId, List<Runner> list)
    {
        super(ctx,resId,list);
        this.resId=resId;
        inflater=LayoutInflater.from(ctx);
    }

    public void setLocationManager(LocationManager lManager)
    {
        this.lManager=lManager;
    }

    public void setUser(String user)
    {
        this.user=user;
    }

    public void setGoogleMap(GoogleMap gMap)
    {
        this.gMap=gMap;
    }

    public void setFloatingActionButton(FloatingActionButton clearButton)
    {
        this.clearButton=clearButton;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view==null)
            view=inflater.inflate(resId,null);

        Runner runner=getItem(position);
        CircleImageView profileImg=(CircleImageView)view.findViewById(R.id.userImg);
        TextView tvNames=(TextView)view.findViewById(R.id.names);
        TextView tvInfo=(TextView)view.findViewById(R.id.personalInfo);
        ImageButton pathButton=(ImageButton)view.findViewById(R.id.pathButton);
        //Creazione informazioni per il recupero della richiesta
        List<Object> infos=new ArrayList<>();
        infos.add(runner.getNickname());
        infos.add(runner.isRecipient());
        pathButton.setTag(infos);

        pathButton.setOnClickListener(getShowPathListener());
        profileImg.setImageDrawable(runner.getProfileImage());
        tvNames.setText(runner.getName()+" "+runner.getSurname()+","+runner.getNickname());
        tvInfo.setText(CheckUtils.getAge(runner.getBirthDare())+" anni, Livello "+ LevelMapper.getLevelName(runner.getLevel()));

        return view;
    }

    private View.OnClickListener getShowPathListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    Location location=lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location!=null)
                    {
                        Request_LiveDao reqDao=new Request_LiveDaoImpl();
                        List<Object> infos=(List<Object>)view.getTag();
                        String runner=(String)infos.get(0);
                        boolean isRecipient=(boolean)infos.get(1);

                        RequestLive requestLive;
                        if(isRecipient)
                        {
                            requestLive=reqDao.findByRunnerRecipient(runner,user);
                            Log.i("RECIPIENT",runner);
                            Log.i("APPLICANT",user);
                        }
                        else
                        {
                            requestLive=reqDao.findByRunnerRecipient(user,runner);
                            Log.i("RECIPIENT",user);
                            Log.i("APPLICANT",runner);
                        }

                        directionFinder=new DirectionFinderImpl(getContext(),gMap,R.mipmap.pin_start,R.mipmap.pin_end);
                        //Origine posizione corrente
                        LatLng origin=new LatLng(location.getLatitude(),location.getLongitude());
                        //Destinazione punto di incontro
                        LatLng destination=requestLive.getWaypoint();
                        //Scrittura percorso su mappa
                        directionFinder.executeDraw(origin,destination);
                        clearButton.setOnClickListener(getClearMapListener());
                        //Viene mostrato il bottone per la pulizia della mappa
                        clearButton.setVisibility(View.VISIBLE);
                    }
                }
                catch (SecurityException ex)
                {

                }
            }
        };
    }

    private View.OnClickListener getClearMapListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Viene cancellato il percorso dalla mappa
                directionFinder.clearMap();
                //il fab diviene invisibile
                clearButton.setVisibility(View.INVISIBLE);
            }
        };
    }
}
