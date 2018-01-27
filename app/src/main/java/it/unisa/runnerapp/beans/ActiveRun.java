package it.unisa.runnerapp.beans;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class ActiveRun extends Run
{
    private double estimatedKm;
    private int    estimatedHours;
    private int    estimatedMinutes;

    public ActiveRun()
    {
        super();
    }

    public ActiveRun(int id, LatLng meetingPoint, Date start, Runner master, double estimatedKm, int estimatedHours, int estimatedMinutes)
    {
        super(id,meetingPoint,start,master);
        this.estimatedKm=estimatedKm;
        setEstimatedHours(estimatedHours);
        setEstimatedMinutes(estimatedMinutes);
    }

    public void setEstimatedKm(double estimatedKm)
    {
        this.estimatedKm=estimatedKm;
    }

    public double getEstimatedKm()
    {
        return estimatedKm;
    }

    public void setEstimatedHours(int estimatedHours)
    {
        if(estimatedHours<0||estimatedHours>24)
            throw new IllegalArgumentException("L'ora specificata non è valida");
        else
            this.estimatedHours=estimatedHours;
    }

    public int getEstimatedHours()
    {
        return estimatedHours;
    }

    public void setEstimatedMinutes(int estimatedMinutes)
    {
        if(estimatedMinutes<0||estimatedMinutes>60)
            throw new IllegalArgumentException("I minuti specificati non sono validi");
        else
            this.estimatedMinutes=estimatedMinutes;
    }

    public int getEstimatedMinutes()
    {
        return estimatedMinutes;
    }
}
