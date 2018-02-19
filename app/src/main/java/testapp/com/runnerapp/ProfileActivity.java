package testapp.com.runnerapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import it.unisa.runnerapp.utils.CheckUtils;

public class ProfileActivity extends AppCompatActivity {



    //Component View
    TextView nametw;
    TextView surnametw;
    TextView nicknametw;
    TextView leveltw;
    TextView travelledkmtw;
    ImageView imageprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nametw = findViewById(R.id.name_tw);
        surnametw = findViewById(R.id.surname_tw);
        nicknametw = findViewById(R.id.nickname_tw);
        leveltw = findViewById(R.id.level_tw);
        travelledkmtw = findViewById(R.id.travelledkm_tw);
        imageprofile = findViewById(R.id.masterprofile_img);


        imageprofile.setImageDrawable(MainActivityPV.userlogged.getProfileImage());
        nametw.setText(MainActivityPV.userlogged.getName());
        surnametw.setText(MainActivityPV.userlogged.getSurname());
        nicknametw.setText(MainActivityPV.userlogged.getNickname());
        leveltw.setText(String.valueOf(MainActivityPV.userlogged.getLevel()));
        travelledkmtw.setText(String.valueOf(MainActivityPV.userlogged.getTraveledKilometers()));

    }

}
