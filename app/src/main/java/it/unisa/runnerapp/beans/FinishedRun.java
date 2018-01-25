package it.unisa.runnerapp.beans;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class FinishedRun extends Run
{
    private User   runner;
    private double traveledKm;
    private double burnedCal;
    private double averageSpeed;

    public FinishedRun()
    {
        super();
    }

    public FinishedRun(int id, LatLng meetingPoint, Date start, User master,User runner,double traveledKm,double burnedCal,double averageSpeed)
    {
        super(id,meetingPoint,start,master);
        this.runner=runner;
        this.traveledKm=traveledKm;
        this.burnedCal=burnedCal;
        this.averageSpeed=averageSpeed;
    }

    public void setRunner(User runner)
    {
        this.runner=runner;
    }

    public User getRunner()
    {
        return runner;
    }

    public void setTraveledKm(double traveledKm)
    {
        this.traveledKm=traveledKm;
    }

    public double getTraveledKm()
    {
        return traveledKm;
    }

    public void setBurnedCal(double burnedCal)
    {
        this.burnedCal=burnedCal;
    }

    public double getBurnedCal()
    {
        return burnedCal;
    }

    public void setAverageSpeed(double averageSpeed)
    {
        this.averageSpeed=averageSpeed;
    }

    public double getAverageSpeed()
    {
        return averageSpeed;
    }

}
