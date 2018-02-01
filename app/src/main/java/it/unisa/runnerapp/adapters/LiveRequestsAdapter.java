package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;
import testapp.com.runnerapp.R;

public class LiveRequestsAdapter extends ArrayAdapter<LiveRequest>
{
    private int              resId;
    private LayoutInflater   inflater;
    private FirebaseDatabase database;
    private String           user;

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
        Date dob=sender.getBirthDare();
        profileImage.setImageDrawable(sender.getProfileImage());
        tvNames.setText(sender.getName()+" "+sender.getSurname()+","+sender.getNickname());
        tvPersonalInfo.setText(CheckUtils.getAge(dob)+" anni,Livello ");
        tvDate.setText("Inviata il "+CheckUtils.parseDate("dd-MM-yyyy",dob));
        //Tag per poter accettare o rifiutare la richiesta
        refuseButton.setTag(sender.getNickname());
        acceptButton.setTag(sender.getNickname());

        refuseButton.setOnClickListener(getRefuseRequestListener());
        acceptButton.setOnClickListener(getAcceptRequestListener());

        String hours=CheckUtils.parseHourOrMinutes(CheckUtils.getHour(dob));
        String minutes=CheckUtils.parseHourOrMinutes(CheckUtils.getMinutes(dob));
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
                removeValue(sender);
            }
        };
    }

    private void removeValue(String sender)
    {
        for(int i=0;i<getCount();i++)
        {
            LiveRequest lr=getItem(i);
            if(lr.getSender().getNickname().equals(sender))
            {
                remove(lr);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
