package testapp.com.runnerapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import it.unisa.runnerapp.fragments.LoginFragment;
import it.unisa.runnerapp.fragments.RegistrationFragment;

public class AuthActivity extends CheckPermissionActivity implements LoginFragment.Communicator {


    //Fragments
    RegistrationFragment regfrag;
    LoginFragment logfrag;
    FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        fm = getFragmentManager();

        logfrag = new LoginFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.containerfragment_frame, logfrag);
        ft.commit();

        logfrag.setCommunicator(this);


    }


    @Override
    public void goRegistration() {

        regfrag = new RegistrationFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.containerfragment_frame, regfrag);
        ft.commit();


    }
}
