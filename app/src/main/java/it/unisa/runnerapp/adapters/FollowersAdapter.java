package it.unisa.runnerapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.logging.Level;

import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import it.unisa.runnerapp.utils.LevelMapper;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 04/02/2018.
 */

public class FollowersAdapter extends ArrayAdapter<Runner> {

    private LayoutInflater inflater;


    public FollowersAdapter(@NonNull Context context, int resource, List<Runner> runner) {
        super(context, resource, runner);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {

            v = inflater.inflate(R.layout.row_follower, parent, false);
            TextView nicktw = (TextView) v.findViewById(R.id.nick_tw);
            TextView leveltw = (TextView) v.findViewById(R.id.level_tw);
            ImageView runnerprofileimg = (ImageView) v.findViewById(R.id.profilerunner_img);

            Runner runnercurrent = getItem(position);
            nicktw.setText(runnercurrent.getNickname());
            leveltw.setText("Liv. " + LevelMapper.getLevelName(runnercurrent.getLevel()));
            runnerprofileimg.setImageDrawable(runnercurrent.getProfileImage());

        }


        return v;
    }
}
