package testapp.com.runnerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {

    //Component View
    TextView nametw;
    TextView surnametw;
    TextView nicknametw;
    TextView leveltw;
    TextView travelledkmtw;
    ImageView imageprofile;
    ImageButton logoutprofilebtn;

    FirebaseAuth firebaseauth;
    FirebaseUser firebaseuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseauth = FirebaseAuth.getInstance();
        firebaseuser = firebaseauth.getCurrentUser();
        //Set view
        nametw = findViewById(R.id.name_tw);
        surnametw = findViewById(R.id.surname_tw);
        nicknametw = findViewById(R.id.nickname_tw);
        leveltw = findViewById(R.id.level_tw);
        travelledkmtw = findViewById(R.id.travelledkm_tw);
        imageprofile = findViewById(R.id.masterprofile_img);
        logoutprofilebtn = findViewById(R.id.logout_btn);


        //Set values
        imageprofile.setImageDrawable(MainActivityPV.userlogged.getProfileImage());
        nametw.setText(MainActivityPV.userlogged.getName());
        surnametw.setText(MainActivityPV.userlogged.getSurname());
        nicknametw.setText(MainActivityPV.userlogged.getNickname());
        leveltw.setText(String.valueOf(MainActivityPV.userlogged.getLevel()));
        DecimalFormat formatter=new DecimalFormat("##.##");
        travelledkmtw.setText(formatter.format(MainActivityPV.userlogged.getTraveledKilometers()));


        //Set Listeners
        logoutprofilebtn.setOnClickListener(getLogoutClickListener());

    }



    public View.OnClickListener getLogoutClickListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                signOut();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
                startActivity(intent);
                //onDestroy();
            }
        };


    }


    private void signOut() {
        // Firebase sign out
        firebaseuser.unlink(firebaseuser.getProviderId());
        firebaseauth.signOut();
    }

}
