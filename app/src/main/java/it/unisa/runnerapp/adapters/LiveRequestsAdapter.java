package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unisa.runnerapp.Dao.Implementation.Request_LiveDaoImpl;
import it.unisa.runnerapp.Dao.Interf.Request_LiveDao;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.RequestLive;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.DirectionFinderImpl;
import it.unisa.runnerapp.utils.LevelMapper;
import it.unisa.runnerapp.utils.RunnersDatabases;
import testapp.com.runnerapp.R;

public class LiveRequestsAdapter extends ArrayAdapter<LiveRequest>
{
    private int              resId;
    private LayoutInflater   inflater;
    private FirebaseDatabase database;
    private String           user;

    private LocationManager        lManager;
    private HashMap<String,Marker> nearbyRunners;

    private AcceptedRequestsAdapter acceptedRequestsAdapter;

    public LiveRequestsAdapter(Context ctx, int resId, List<LiveRequest> list)
    {
        super(ctx,resId,list);
        this.resId=resId;
        inflater=LayoutInflater.from(ctx);
    }

    public void setDatabase(FirebaseDatabase database)
    {
        this.database=database;
    }

    public void setUser(String user)
    {
        this.user=user;
    }

    public void setLocationManager(LocationManager lManager)
    {
        this.lManager=lManager;
    }

    public void setNearbyRunners(HashMap<String,Marker> nearbyRunners)
    {
        this.nearbyRunners=nearbyRunners;
    }

    public void setAcceptedRequestsAdapter(AcceptedRequestsAdapter acceptedRequestsAdapter)
    {
        this.acceptedRequestsAdapter=acceptedRequestsAdapter;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if(view==null)
            view=inflater.inflate(resId,null);

        LiveRequest request=getItem(position);

        CircleImageView profileImage=(CircleImageView)view.findViewById(R.id.senderImg);
        TextView tvNames=(TextView)view.findViewById(R.id.names);
        TextView tvPersonalInfo=(TextView)view.findViewById(R.id.personalInfo);
        TextView tvDate=(TextView)view.findViewById(R.id.dateInfo);
        TextView tvHour=(TextView)view.findViewById(R.id.hourInfo);
        ImageButton refuseButton=(ImageButton)view.findViewById(R.id.refuse);
        ImageButton acceptButton=(ImageButton)view.findViewById(R.id.accept);

        Runner sender=request.getSender();
        Date sendingDate=request.getSendingDate();
        profileImage.setImageDrawable(sender.getProfileImage());
        tvNames.setText(sender.getName()+" "+sender.getSurname()+","+sender.getNickname());
        tvPersonalInfo.setText(CheckUtils.getAge(sender.getBirthDare())+" anni,Livello "+ LevelMapper.getLevelName(sender.getLevel()));
        tvDate.setText("Inviata il "+CheckUtils.parseDate("dd-MM-yyyy",sendingDate));
        //Setto nickname come tag per poter accettare o rifiutare la richiesta
        //con maggiore semplicità
        refuseButton.setTag(sender.getNickname());
        acceptButton.setTag(sender.getNickname());

        refuseButton.setOnClickListener(getRefuseRequestListener());
        acceptButton.setOnClickListener(getAcceptRequestListener());

        String hours=CheckUtils.parseHourOrMinutes(CheckUtils.getHour(sendingDate));
        String minutes=CheckUtils.parseHourOrMinutes(CheckUtils.getMinutes(sendingDate));
        tvHour.setText("Alle "+hours+":"+minutes);

        return view;
    }

    private View.OnClickListener getRefuseRequestListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Ci spostiamo sul nodo associato all'utente che accetta la richiesta
                DatabaseReference dbReference=database.getReference(RunnersDatabases.LIVE_REQUEST_DB_ROOT+"/"+user);
                String sender=(String)view.getTag();
                dbReference.child(sender).removeValue();
                removeValue(sender);
            }
        };
    }

    private View.OnClickListener getAcceptRequestListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Ci spostiamo sul nodo associato all'utente che accetta la richiesta
                DatabaseReference dbReference=database.getReference(RunnersDatabases.LIVE_REQUEST_DB_ROOT+"/"+user);
                String sender=(String)view.getTag();
                //Ci spostiamo sul nodo associato a chi ha inviato alla richiesta e
                //successivamente sul nodo che sarà associato alla risposta
                dbReference=dbReference.child(sender+"/"+RunnersDatabases.LIVE_REQUEST_DB_ANSWER_NODE);
                dbReference.setValue(RunnersDatabases.LIVE_REQEUEST_DB_REQUEST_ACCEPTED);
                //Rimozione elemento dal listview ed aggiunta dello stesso alla
                //lista delle richieste accettate
                LiveRequest lr=removeValue(sender);
                Runner runner=lr.getSender();

                if(acceptedRequestsAdapter!=null)
                {
                    runner.isRecipient(false);
                    acceptedRequestsAdapter.add(runner);
                    acceptedRequestsAdapter.notifyDataSetChanged();
                    //Creazione punto intermedio e memorizzazione in db
                    try
                    {
                        Location location=lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Marker marker=nearbyRunners.get(sender);
                        Log.i("LOCATION",""+location);
                        Log.i("MARKERZ",""+marker);
                        if(location!=null&&marker!=null)
                        {
                            LatLng origin=new LatLng(location.getLatitude(),location.getLongitude());
                            LatLng destination=marker.getPosition();
                            Log.i("ORIGINE",""+origin);
                            Log.i("DESTINAZIONE",""+destination);
                            DirectionFinderImpl directionFinder=new DirectionFinderImpl();
                            LatLng midPoint=directionFinder.execute(origin,destination,true);
                            Log.i("MIDPOINT",""+midPoint);
                            Request_LiveDao reqDao=new Request_LiveDaoImpl();
                            Runner recipient=new Runner();
                            recipient.setNickname(user);
                            Runner applicant=new Runner();
                            applicant.setNickname(sender);
                            reqDao.createRequestLive(new RequestLive(applicant,recipient,midPoint));
                        }
                        else
                        {
                            Log.i("MEX","Runner non in zona,impossibile accettare la richiesta");
                        }
                    }
                    catch (SecurityException ex)
                    {
                    }
                }
            }
        };
    }

    private LiveRequest removeValue(String sender)
    {
        LiveRequest lr=null;

        for(int i=0;i<getCount();i++)
        {
            lr=getItem(i);
            if(lr.getSender().getNickname().equals(sender))
            {
                remove(lr);
                notifyDataSetChanged();
                break;
            }
        }

        return lr;
    }
}
