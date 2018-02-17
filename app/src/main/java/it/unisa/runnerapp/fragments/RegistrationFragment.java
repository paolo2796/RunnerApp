package it.unisa.runnerapp.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import testapp.com.runnerapp.R;

/**
 * Created by Paolo on 17/02/2018.
 */

public class RegistrationFragment extends Fragment {


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
        paswet =  v.findViewById(R.id.passreg_et);
        profileloadbtn = v.findViewById(R.id.profileload_btn);
        name_et = v.findViewById(R.id.namereg_et);
        surname_et = v.findViewById(R.id.surnamereg_et);
        datebirth_et = v.findViewById(R.id.datebirthreg_btn);
        weight_et = v.findViewById(R.id.weightreg_et);
        level_sp = (Spinner) v.findViewById(R.id.levelreg_sp);
        nicket = v.findViewById(R.id.nickreg_et);


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


        return v;
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

}
