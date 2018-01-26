package it.unisa.runnerapp.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;

public class GeoUtils
{
    public static final int TWO_MINUTES = 1000 * 60 * 2;

    public static LocationManager getLocationManager(Context ctx)
    {
        return (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationProvider getProvider(LocationManager lm,String provider)
    {
        return lm.getProvider(provider);
    }

    public static LocationProvider getBestProvider(LocationManager lm) throws SecurityException
    {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);
        String provider = lm.getBestProvider(criteria, true);

        if (provider != null)
            return lm.getProvider(provider);
        else
            return getProvider(lm, LocationManager.GPS_PROVIDER);
    }

    public static void startLocationUpdates(LocationManager lm, String provider,int time,int distance, LocationListener listener) throws SecurityException
    {
        lm.requestLocationUpdates(provider,time,distance,listener);
    }

    public static void stopLocationUpdates(LocationManager lm,LocationListener listener)
    {
        lm.removeUpdates(listener);
    }

    public static boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        if (currentBestLocation == null)
        {
            return true;
        }

        // Controlla se il fix della location è nuovo o vecchio
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // Se sono passati più di due minuti dall'ultimo fix viene usata la nuova locazione
        // poichè probabilmente l'utente si è spostato
        if (isSignificantlyNewer)
        {
            return true;
        }
        else if (isSignificantlyOlder)
        {
            return false;
        }

        // Controlla se il nuovo fix è più o meno accurato del vecchio
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Controlla se proviene dello stesso provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determina la qualità della location usando l'accuratezza e l'anzianità
        if (isMoreAccurate)
        {
            return true;
        }
        else if (isNewer && !isLessAccurate)
        {
            return true;
        }
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
        {
            return true;
        }

        return false;
    }

    public static boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null)
        {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
