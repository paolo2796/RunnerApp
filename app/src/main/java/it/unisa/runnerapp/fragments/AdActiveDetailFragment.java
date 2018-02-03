package it.unisa.runnerapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import it.unisa.runnerapp.beans.ActiveRun;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 03/02/2018.
 */

public class AdActiveDetailFragment extends Fragment {

    Communicator communicator;
    private ActiveRun run;
    public static final String ARG_POSITION = "activerun";

    // Views Component
    private TextView nickmastertw;
    private ImageView masterprofileimg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            run = (ActiveRun) getArguments().getSerializable(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adactivedetail_fragment, container, false);

        nickmastertw = (TextView) v.findViewById(R.id.masternickaname_tw);
        masterprofileimg = (ImageView) v.findViewById(R.id.masterprofile_img);
        masterprofileimg.setImageDrawable(run.getMaster().getProfileImage());



        nickmastertw.setText(run.getMaster().getNickname());


        return v;
    }

    public void setCommunicator(Communicator communicator){

        this.communicator = communicator;
    }

    public interface Communicator{

        public void respond(int index);
    }

    public static AdActiveDetailFragment newInstance(ActiveRun run) {
        AdActiveDetailFragment myFragment = new AdActiveDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSITION, run);
        myFragment.setArguments(args);
        return myFragment;
    }

}
