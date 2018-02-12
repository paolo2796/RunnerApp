package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unisa.runnerapp.Dao.Implementation.Request_LiveDaoImpl;
import it.unisa.runnerapp.Dao.Interf.Request_LiveDao;
import it.unisa.runnerapp.beans.RequestLive;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.LevelMapper;
import testapp.com.runnerapp.R;

public class AcceptedRequestsAdapter extends ArrayAdapter<Runner>
{
    private String user;

    private int             resId;
    private LayoutInflater  inflater;
    private LocationManager lManager;

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
                    }
                }
                catch (SecurityException ex)
                {

                }
            }
        };
    }
}
