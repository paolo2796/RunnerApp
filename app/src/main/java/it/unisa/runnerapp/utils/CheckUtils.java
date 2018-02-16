package it.unisa.runnerapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class CheckUtils
{
    public static boolean checkPermissions(Context ctx,String... permissions)
    {
        boolean areGranted=true;
        int index=0;

        while (areGranted&&index<permissions.length)
        {
            areGranted=areGranted&&isPermissionGranted(ctx,permissions[index]);
            index+=1;
        }

        return areGranted;
    }

    public static boolean isPermissionGranted(Context ctx, String permission)
    {
        return ActivityCompat.checkSelfPermission(ctx,permission)== PackageManager.PERMISSION_GRANTED;
    }

    public static String parseDate(String pattern,Date date)
    {
        try
        {
            SimpleDateFormat formatter=new SimpleDateFormat(pattern);
            return formatter.format(date);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static int getHour(Date date)
    {
        Calendar c=new GregorianCalendar();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinutes(Date date)
    {
        Calendar c=new GregorianCalendar();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    public static int getAge(Date date)
    {
        Calendar dob=new GregorianCalendar();
        dob.setTime(date);
        Calendar today = Calendar.getInstance();
        int curYear = today.get(Calendar.YEAR);
        int dobYear = dob.get(Calendar.YEAR);
        int age = curYear - dobYear;
        int curMonth = today.get(Calendar.MONTH);
        int dobMonth = dob.get(Calendar.MONTH);
        if (dobMonth > curMonth)
        {
            age--;
        }
        else if (dobMonth == curMonth)
        {
            int curDay = today.get(Calendar.DAY_OF_MONTH);
            int dobDay = dob.get(Calendar.DAY_OF_MONTH);

            if (dobDay > curDay)
            {
                age--;
            }
        }

        return age;
    }

    public static String parseHourOrMinutes(int x)
    {
        String x_str;
        if(x>=0&&x<=9)
            x_str="0"+x;
        else
            x_str=""+x;
        return x_str;
    }


    public static String convertDateToStringFormat(Date data){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        String month =  capitalizeFirstLetter(new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)]);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.valueOf( day + " " + month);
    }



    public static String convertHMToStringFormat(Date data){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        int hours = CheckUtils.getHour(data);
        int minutes = CheckUtils.getMinutes(data);
        return String.valueOf( CheckUtils.parseHourOrMinutes(hours) + ":" + CheckUtils.parseHourOrMinutes(minutes));
    }



    public static String capitalizeFirstLetter(String mystring){

        return  mystring.substring(0,1).toUpperCase() + mystring.substring(1);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
