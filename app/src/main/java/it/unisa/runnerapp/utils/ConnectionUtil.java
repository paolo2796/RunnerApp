package it.unisa.runnerapp.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Paolo on 27/01/2018.
 */

public class ConnectionUtil {

    private static Connection connection;

    static {
        try {


            try {

                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:mysql://runnerinstance.cszqnohnholf.us-west-2.rds.amazonaws.com:3306/runnerDatabase", "root", "it.uniSA.app_1996_.");

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
