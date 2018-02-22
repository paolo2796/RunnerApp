package it.unisa.runnerapp.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Paolo on 27/01/2018.
 */

public class ConnectionUtil {

    private static Connection connection;

    static {
        try {

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                DriverManager.setLoginTimeout(0);
                //Log.i("Messaggio",String.valueOf(DriverManager.getLoginTimeout()));
                connection = DriverManager.getConnection("jdbc:mysql://" + RunnersDatabases.END_POINT_DB_MYSQL + "/"+RunnersDatabases.NAME_DB_MYSQL + "?autoReconnect=true", RunnersDatabases.USER_DB_MYSQL, RunnersDatabases.PASS_DB_MYSQL);

            }

            catch (SQLException e) {


                e.printStackTrace();
            }
            } catch (Exception e) {
                e.printStackTrace();
            }


    }


    public static Connection getConnection() {


        return connection;
    }

}
