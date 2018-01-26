package it.unisa.runnerapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


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
}
