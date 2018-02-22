package it.unisa.runnerapp.utils;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils
{
    public static FirebaseApp getFirebaseApp(Context ctx,String appId, String apiKey, String dbUrl, String dbName)
    {
        FirebaseOptions fbOpt=new FirebaseOptions.Builder()
                .setApplicationId(appId)
                .setApiKey(apiKey)
                .setDatabaseUrl(dbUrl)
                .build();
        FirebaseApp.initializeApp(ctx,fbOpt,dbName);
        return FirebaseApp.getInstance(dbName);
    }

    public static FirebaseDatabase connectToDatabase(FirebaseApp app)
    {
        return FirebaseDatabase.getInstance(app);
    }

}
