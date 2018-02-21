package testapp.com.runnerapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.fragments.LoginFragment;
import it.unisa.runnerapp.fragments.RegistrationFragment;
import it.unisa.runnerapp.utils.FirebaseUtils;
import it.unisa.runnerapp.utils.RunnersDatabases;

public class AuthActivity extends CheckPermissionActivity implements LoginFragment.Communicator, RegistrationFragment.Communicator {


    //Fragments
    RegistrationFragment regfrag;
    LoginFragment logfrag;
    FragmentManager fm;

    static RelativeLayout authrl;

    // DB Firebase
    public static DatabaseReference databaseusers;
    public static FirebaseApp firebaseapp;
    public static FirebaseDatabase firebasedatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authrl = (RelativeLayout) findViewById(R.id.authrl);
        if (firebaseapp == null){
            firebaseapp = FirebaseUtils.getFirebaseApp(this.getApplicationContext(), RunnersDatabases.USERS_API_KEY, RunnersDatabases.USERS_APP_ID, RunnersDatabases.USERS_DB_URL, RunnersDatabases.USERS_DB_NAME);
            firebasedatabase = FirebaseUtils.connectToDatabase(firebaseapp);
            databaseusers = firebasedatabase.getReference("Users");
        }

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

    @Override
    public void sendLogin() {
        logfrag = new LoginFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.containerfragment_frame, logfrag);
        ft.commit();
    }


    @Override
    public void goHome(String nickname){
        MainActivityPV.userlogged = new RunnerDaoImpl().getByNick(nickname);
        Intent intent = new Intent(this, MainActivityPV.class);
        startActivity(intent);
        finish();
    }

    public static void setAlphaAuthRL(float alpha){
        authrl.setAlpha(alpha);
    }
}
