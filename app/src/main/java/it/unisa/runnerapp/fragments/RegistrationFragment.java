package it.unisa.runnerapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.unisa.runnerapp.Dao.Implementation.RunnerDaoImpl;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.CheckUtils;
import testapp.com.runnerapp.AuthActivity;
import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 17/02/2018.
 */

public class RegistrationFragment extends Fragment {

    //Firebase
    FirebaseAuth firebaseauth;


    //Result Code
    public static final int RESULT_GALLERY = 0;

    //Communicator
    private Communicator communicator;



    //Component View
    EditText emailet;
    EditText paswet;
    Button profileloadbtn;
    EditText name_et;
    EditText surname_et;
    Button datebirth_et;
    EditText weight_et;
    Spinner level_sp;
    EditText nicket;
    Button sendregistration;

    //Util
    HashMap<String,Boolean> checkfield;
    Drawable imageprofile;
    AlertDialog.Builder dialogsuccessregistration;


    //Calendar
    Calendar mdate;
    DatePickerDialog datepickerdialog;


    ArrayAdapter<String> itemleveladapter;
    ArrayList<String> itemslevel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.registration_fragment, container, false);

        emailet = v.findViewById(R.id.emailreg_et);
        emailet.setTag("email");
        paswet =  v.findViewById(R.id.passreg_et);
        profileloadbtn = v.findViewById(R.id.profileload_btn);
        name_et = v.findViewById(R.id.namereg_et);
        surname_et = v.findViewById(R.id.surnamereg_et);
        datebirth_et = v.findViewById(R.id.datebirthreg_btn);
        weight_et = v.findViewById(R.id.weightreg_et);
        level_sp = (Spinner) v.findViewById(R.id.levelreg_sp);
        nicket = v.findViewById(R.id.nickreg_et);
        nicket.setTag("nickname");
        sendregistration = v.findViewById(R.id.sendregistration_btn);


        mdate = Calendar.getInstance();
        datebirth_et.setOnClickListener(getDateBirthClickListener());
        level_sp.setSelection(0);

        itemslevel = new ArrayList<String>();
        itemslevel.add("Principiante");
        itemslevel.add("Dilettante");
        itemslevel.add("Esperto");
        itemslevel.add("Maratoner");

        itemleveladapter = new ArrayAdapter<String>(getActivity(), R.layout.row_itemlevel, itemslevel);
        itemleveladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level_sp.setAdapter(itemleveladapter);


        // Set Listener
        emailet.setOnFocusChangeListener(getFocusEmailListener());
        nicket.setOnFocusChangeListener(getFocusNickListener());
        profileloadbtn.setOnClickListener(getProfileLoadkListener());
        weight_et.setOnFocusChangeListener(getFocusWeightListener());
        name_et.setOnFocusChangeListener(getFocusNameListener());
        surname_et.setOnFocusChangeListener(getFocusSurnameListener());
        paswet.setOnFocusChangeListener(getFocusPassListener());
        sendregistration.setOnClickListener(getsendRegistrationClick());

        initCheckField();



        return v;
    }


    public interface Communicator{
        public void sendLogin();
    }

    public void setCommunicator(RegistrationFragment.Communicator communicator){
        this.communicator = communicator;
    }




    public View.OnClickListener getsendRegistrationClick(){


        return new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendregistration.requestFocus();
                   /* Log.i("Messaggio",String.valueOf(sendregistration.isFocusable()));
                    Log.i("Messaggio bol", checkfield.get("email").toString());
                    Log.i("Messaggio bol", checkfield.get("nickname").toString());
                    Log.i("Messaggio bol", checkfield.get("password").toString());
                    Log.i("Messaggio bol", checkfield.get("name").toString());
                    Log.i("Messaggio bol", checkfield.get("surname").toString());
                    Log.i("Messaggio bol", checkfield.get("datebirth").toString());
                    Log.i("Messaggio bol", checkfield.get("weight").toString());
                    Log.i("Messaggio bol", checkfield.get("imageprofile").toString());
                    Log.i("Messaggio bol", checkfield.get("level").toString()); */


                        if(checkfield.get("email") && checkfield.get("nickname") && checkfield.get("password") && checkfield.get("name")
                                && checkfield.get("surname") && checkfield.get("datebirth") && checkfield.get("weight")
                                && checkfield.get("level") && checkfield.get("imageprofile")){

                            sendregistration.setText("Attendi...");


                            Runner runner = new Runner(nicket.getText().toString(),paswet.getText().toString(),name_et.getText().toString(),
                                    surname_et.getText().toString(),imageprofile,new Date(mdate.getTimeInMillis()),Double.parseDouble(weight_et.getText().toString()),
                                    0, CheckUtils.convertLevelFromStringToShort((String)level_sp.getSelectedItem()));

                            new RunnerDaoImpl().createRunner(runner);
                            saveUserFirebase(runner.getNickname(),emailet.getText().toString(),runner.getPassword());


                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                            // set title
                            alertDialogBuilder.setTitle("Registrazione Completata!");

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage("Accedi")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            communicator.sendLogin();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }

                }
            };
        }

    public View.OnFocusChangeListener getFocusNameListener(){


        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!((EditText)v).getText().toString().equals(""))
                    checkfield.put("name",true);
                else
                    checkfield.put("name",false);
            }
        };
    }

    public View.OnFocusChangeListener getFocusPassListener(){


        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if((((EditText)v).getText().toString().length())>=6)
                    checkfield.put("password",true);
                else
                    checkfield.put("password",false);
            }
        };
    }


    public View.OnFocusChangeListener getFocusSurnameListener(){


        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!((EditText)v).getText().toString().equals(""))
                    checkfield.put("surname",true);
                else
                    checkfield.put("surname",false);
            }
        };
    }

    public View.OnFocusChangeListener getFocusWeightListener(){

        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                    if(!(((EditText)v).getText().toString().equals(""))) {
                        checkfield.put("weight", true);
                        Log.i("Messaggio","SONO QUI");
                    }
                    else {
                        checkfield.put("weight", false);
                        Log.i("Messaggio","SONO FALSE");
                    }
            }
        };
    }

    public View.OnClickListener getProfileLoadkListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , RESULT_GALLERY );
            }
        };
    }





    public View.OnFocusChangeListener getFocusEmailListener(){

        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {

                if(!hasFocus){


                    final EditText emailet =((EditText)v);
                    final String email = emailet.getText().toString();

                    if(isValidEmail(email)){
                        Query query = AuthActivity.databaseusers.orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(getValueEventByEmail(emailet));
                    }

                    else{

                        v.setBackgroundColor(Color.GRAY);
                        emailet.setText("");
                        emailet.setHint("Campo non corretto!");
                        checkfield.put("email",false);

                    }
                }

            }
        };
    }



    public View.OnFocusChangeListener getFocusNickListener(){

        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {

                if(!hasFocus){
                    final EditText nickname_et =((EditText)v);
                    final String nickname = nickname_et.getText().toString().trim();

                    if(isValidNickName(nickname)){

                        Query query = AuthActivity.databaseusers.orderByChild("nickname").equalTo(nickname);
                        query.addListenerForSingleValueEvent(getValueEventByNickname(nickname_et));
                    }

                    else{

                        nickname_et.setBackgroundColor(Color.GRAY);
                        nickname_et.setText("");
                        nickname_et.setHint("Campo non corretto!");
                        checkfield.put("nickname",false);
                    }


                }

            }
        };
    }



    public ValueEventListener getValueEventByEmail(final EditText edit){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==0){
                    edit.setBackgroundColor(Color.GREEN);
                    checkfield.put("email",true);
                }
                else{

                    edit.setBackgroundColor(Color.RED);
                    edit.setText("");
                    edit.setHint("Email già in uso! Riprova!");
                    checkfield.put("email",false);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }




    public ValueEventListener getValueEventByNickname(final EditText edit){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==0){
                    edit.setBackgroundColor(Color.GREEN);
                    checkfield.put("nickname",true);
                }
                else{

                    edit.setBackgroundColor(Color.RED);
                    edit.setText("");
                    edit.setHint("Username già in uso!");
                    checkfield.put("nickname",false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }





    public View.OnClickListener getDateBirthClickListener(){

            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mdate.set(Calendar.YEAR, year);
                                mdate.set(Calendar.MONTH, monthOfYear);
                                mdate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                checkfield.put("datebirth",true);
                                updateDate();
                            }
                        };

                        datepickerdialog = new DatePickerDialog(getActivity(), R.style.DialogThemeDateTime, mDateListener, mdate.get(Calendar.YEAR), mdate.get(Calendar.MONTH), mdate.get(Calendar.DAY_OF_MONTH));
                        datepickerdialog.show();
                    }
            };
    }









    public void updateDate(){

        datebirth_et.setText(DateUtils.formatDateTime(getActivity(), mdate.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE));
    }


    private void saveUserFirebase(String nickname, String email, String password){

        Map map = new HashMap();
        map.put("nickname",nickname);
        map.put("email",email);

        AuthActivity.databaseusers.child(nickname).updateChildren(map);
        firebaseauth = FirebaseAuth.getInstance();

        Log.i("FirebaseAuth",String.valueOf(firebaseauth));
        firebaseauth.createUserWithEmailAndPassword(email,password);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RegistrationFragment.RESULT_GALLERY :
                if (data != null) {
                    try {
                        Uri datauri = data.getData();
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(datauri);

                        imageprofile = Drawable.createFromStream(inputStream, datauri.toString());
                        profileloadbtn.setBackground(imageprofile);
                        checkfield.put("imageprofile",true);

                    } catch (FileNotFoundException e) {
                        Log.e("Error","Error upload image");
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean isValidEmail(String email){

        String regex ="^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);

    }

    private boolean isValidNickName(String nickname){

        String regex = "^[A-Za-z0-9-_@]+";
        return nickname.matches(regex);
    }


    private void initCheckField(){
        checkfield = new HashMap<String,Boolean>();
        checkfield.put("email",false);
        checkfield.put("nickname",false);
        checkfield.put("password",false);
        checkfield.put("name",false);
        checkfield.put("surname",false);
        checkfield.put("datebirth",false);
        checkfield.put("weight",false);
        checkfield.put("level",true);
        checkfield.put("imageprofile",false);

    }

}
