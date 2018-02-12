package it.unisa.runnerapp.beans;

import com.google.android.gms.maps.model.LatLng;

import java.sql.ResultSet;

/**
 * Created by Paolo on 09/02/2018.
 */

public class RequestLive {

    private int cod;
    private Runner runner_applicant;
    private Runner runner_recipient;
    private LatLng waypoint;

    public RequestLive(){}

    public RequestLive(int cod,Runner runner_applicant,Runner runner_recipient, LatLng waypoint){
        this.cod = cod;
        this.runner_applicant = runner_applicant;
        this.runner_recipient = runner_recipient;
        this.waypoint = waypoint;
    }
    public RequestLive(Runner runner_applicant,Runner runner_recipient, LatLng waypoint){
        this.runner_applicant = runner_applicant;
        this.runner_recipient = runner_recipient;
        this.waypoint = waypoint;
    }

    public Runner getRunner_applicant(){return runner_applicant;}

    public Runner getRunner_recipient(){return runner_recipient;}


    public LatLng getWaypoint(){return waypoint;}


    public int getCod(){return cod;}
}
