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
                connection = DriverManager.getConnection("jdbc:mysql://" + RunnersDatabases.END_POINT_DB_MYSQL + "/"+RunnersDatabases.NAME_DB_MYSQL, RunnersDatabases.USER_DB_MYSQL, RunnersDatabases.PASS_DB_MYSQL);

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
