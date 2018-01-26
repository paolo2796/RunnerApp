package it.unisa.runnerapp.beans;

public class GeoUser
{
    private String   nickname;
    private double   latitude;
    private double   longitude;

    public GeoUser()
    {
    }

    public GeoUser(String nickname)
    {
        this.nickname=nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname=nickname;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setLatitude(double latitude)
    {
        this.latitude=latitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude=longitude;
    }

    public double getLongitude()
    {
        return longitude;
    }
}
