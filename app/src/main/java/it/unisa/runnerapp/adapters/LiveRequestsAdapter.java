package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.R;

public class LiveRequestsAdapter extends ArrayAdapter<LiveRequest>
{
    private int            resId;
    private LayoutInflater inflater;

    public LiveRequestsAdapter(Context ctx, int resId, List<LiveRequest> list)
    {
        super(ctx,resId,list);
        this.resId=resId;
        inflater=LayoutInflater.from(ctx);
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

        refuseButton.setOnClickListener(getRefuseRequestListener());
        acceptButton.setOnClickListener(getAcceptRequestListener());

        String hours=CheckUtils.parseHourOrMinutes(CheckUtils.getHour(dob));
        String minutes=CheckUtils.parseHourOrMinutes(CheckUtils.getMinutes(dob));
        tvHour.setText("Alle "+hours+":"+minutes);

        return view;
    }

    private View.OnClickListener getRefuseRequestListener()
    {
        return null;
    }

    private View.OnClickListener getAcceptRequestListener()
    {
        return null;
    }
}
