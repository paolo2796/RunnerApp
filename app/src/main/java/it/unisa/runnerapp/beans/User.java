package it.unisa.runnerapp.beans;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class User
{
    private String   email;
    private String   password;
    private String   name;
    private String   surname;
    private Drawable img;
    private Date     birthDate;
    private double   weight;
    private double   traveledKm;
    private short    level;

    public User()
    {
    }

    public User(String email,String password,String name,String surname,Drawable img,Date birthDate,double weight,double traveledKm,short level)
    {
        this.email=email;
        this.password=password;
        this.name=name;
        this.surname=surname;
        this.img=img;
        this.birthDate=birthDate;
        this.weight=weight;
        this.traveledKm=traveledKm;
        this.level=level;
    }

    public void setEmail(String email)
    {
        this.email=email;
    }

    public String getEmail()
    {
        return email;
    }

    public void setPassword(String password)
    {
        this.password=password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public String getName()
    {
        return name;
    }

    public void setSurname(String surname)
    {
        this.surname=surname;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setProfileImage(Drawable img)
    {
        this.img=img;
    }

    public Drawable getProfileImage()
    {
        return img;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate=birthDate;
    }

    public Date getBirthDare()
    {
        return birthDate;
    }

    public void setWeight(double weight)
    {
        this.weight=weight;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setTraveledKilometers(double traveledKm)
    {
        this.traveledKm=traveledKm;
    }

    public double getTraveledKilometers()
    {
        return traveledKm;
    }

    public void setLevel(short level)
    {
        this.level=level;
    }

    public short getLevel()
    {
        return level;
    }
}
