package testapp.com.runnerapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class LoginActivity extends AppCompatActivity {

    //Component View
    EditText useret;
    EditText passet;
    Button loginbtn;
    Button registrationbtn;
    RelativeLayout loginrl;
    RelativeLayout registrationrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginrl = (RelativeLayout) findViewById(R.id.login);
        registrationrl = (RelativeLayout) findViewById(R.id.registration);

        useret = (EditText) findViewById(R.id.user_tw);
        passet = (EditText) findViewById(R.id.pass_tw);
        loginbtn = (Button) findViewById(R.id.login_btn);
        registrationbtn = (Button) findViewById(R.id.registration_btn);


        registrationbtn.setOnClickListener(getRegistrationClickListener());










    }



    public View.OnClickListener getRegistrationClickListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        };
    }

}
