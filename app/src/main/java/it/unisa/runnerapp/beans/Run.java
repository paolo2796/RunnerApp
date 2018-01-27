package it.unisa.runnerapp.beans;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Run
{
    private int    id;
    private LatLng meetingPoint;
    private Date   start;
    private Runner master;

    public Run()
    {
    }

    public Run(int id,LatLng meetingPoint,Date start,Runner master)
    {
        this.id=id;
        this.meetingPoint=meetingPoint;
        this.start=start;
        this.master=master;
    }

    public Run(LatLng meetingPoint,Date start,Runner master)
    {
        this.id=id;
        this.meetingPoint=meetingPoint;
        this.start=start;
        this.master=master;
    }

    public void setId(int id)
    {
        this.id=id;
    }

    public int getId()
    {
        return id;
    }

    public void setMeetingPoint(LatLng meetingPoint)
    {
        this.meetingPoint=meetingPoint;
    }

    public LatLng getMeetingPoint()
    {
        return meetingPoint;
    }

    public void setStartDate(Date start)
    {
        this.start=start;
    }

    public Date getStartDate()
    {
        return start;
    }

    public void setMaster(Runner master)
    {
        this.master=master;
    }

    public Runner getMaster()
    {
        return master;
    }
}