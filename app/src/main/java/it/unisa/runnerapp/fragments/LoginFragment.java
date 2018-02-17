package it.unisa.runnerapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 17/02/2018.
 */

public class LoginFragment extends Fragment {


    //Component View
    EditText useret;
    EditText passet;
    Button loginbtn;
    Button registrationbtn;
    RelativeLayout loginrl;
    RelativeLayout registrationrl;


    Communicator communicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.login_fragment, container, false);

        loginrl = (RelativeLayout) v.findViewById(R.id.login);
        registrationrl = (RelativeLayout) v.findViewById(R.id.registration);

        useret = (EditText) v.findViewById(R.id.user_tw);
        passet = (EditText) v.findViewById(R.id.pass_tw);
        loginbtn = (Button) v.findViewById(R.id.login_btn);
        registrationbtn = (Button) v.findViewById(R.id.registration_btn);

        registrationbtn.setOnClickListener(getRegistrationClickListener());

        return v;
    }



    public View.OnClickListener getRegistrationClickListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                communicator.goRegistration();
            }
        };
    }


    public interface Communicator{

        public void goRegistration();
    }


    public void setCommunicator(Communicator communicator){
        this.communicator = communicator;
    }


}
