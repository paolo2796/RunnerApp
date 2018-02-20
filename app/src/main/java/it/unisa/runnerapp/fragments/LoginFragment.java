package it.unisa.runnerapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import testapp.com.runnerapp.AuthActivity;
import testapp.com.runnerapp.MainActivityPV;
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
    AVLoadingIndicatorView loadingsignin;

     FirebaseAuth firebaseauth;
     FirebaseUser firebaseuser;



    Communicator communicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.login_fragment, container, false);

        firebaseauth = FirebaseAuth.getInstance();
        firebaseuser = firebaseauth.getCurrentUser();
        loadingsignin = (AVLoadingIndicatorView) v.findViewById(R.id.loading_signin);

        useret = (EditText) v.findViewById(R.id.user_tw);
        passet = (EditText) v.findViewById(R.id.pass_tw);
        loginbtn = (Button) v.findViewById(R.id.login_btn);
        registrationbtn = (Button) v.findViewById(R.id.registration_btn);


        if(firebaseuser !=null) {

            AuthActivity.databaseusers.orderByChild("email").equalTo(firebaseuser.getEmail()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    communicator.goHome(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        //Set Listener
        registrationbtn.setOnClickListener(getRegistrationClickListener());
        loginbtn.setOnClickListener(getLoginClickListener());



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


    public View.OnClickListener getLoginClickListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regexemail ="^[A-Za-z0-9+_.-]+@(.+)$";
                final String email = useret.getText().toString().trim();
                String password = passet.getText().toString().trim();

                if(!password.equalsIgnoreCase("") && email.matches(regexemail)){
                    AuthActivity.setAlphaAuthRL((float) 0.5);
                    loadingsignin.show();

                    firebaseauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                AuthActivity.databaseusers.orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        AuthActivity.setAlphaAuthRL(1);
                                        loadingsignin.hide();
                                        communicator.goHome(dataSnapshot.getKey());
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        Log.i("onChildChanged",s);

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                                        Log.i("onChildRemoved",dataSnapshot.getKey());

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                        Log.i("onChildMoved",s);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{

                                Toast.makeText(getActivity(),"Email o Password errata/i!",Toast.LENGTH_LONG).show();
                                AuthActivity.setAlphaAuthRL(1);
                                loadingsignin.hide();
                            }


                        }
                    });
                }

                else{

                    Toast.makeText(getActivity(),"Alcuni campi non sono corretti!",Toast.LENGTH_LONG).show();
                }

            }
        };


    }


    public interface Communicator{

        public void goRegistration();
        public void goHome(String nickname);
    }


    public void setCommunicator(Communicator communicator){
        this.communicator = communicator;
    }


}
