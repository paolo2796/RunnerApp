package it.unisa.runnerapp.Dao.Implementation;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.ConnectionUtil;

/**
 * Created by Paolo on 27/01/2018.
 */

public class RunnerDaoImplT extends AsyncTask<Void,Void,List<Runner>>{


    @Override
    protected List<Runner> doInBackground(Void... voids) {

        ResultSet rs =null;
        List<Runner> runners = new ArrayList<Runner>();
        PreparedStatement ps = null;
        try {

            ps = ConnectionUtil.getConnection().prepareStatement("select * from Utenti");
            rs = ps.executeQuery();


            while (rs.next()) {


                Runner runner = new Runner();

                runner.setEmail(rs.getString("email"));
                runner.setPassword(rs.getString("password"));
                runner.setName(rs.getString("nome"));
                runner.setSurname(rs.getString("cognome"));
                runner.setBirthDate(rs.getDate("data_nascita"));
                runner.setWeight(rs.getDouble("peso"));
                runner.setLevel(rs.getShort("livello"));
                runner.setTraveledKilometers(rs.getDouble("km_percorsi"));


                runners.add(runner);

            }

        }


        catch (SQLException e) {
            e.printStackTrace();
        }
        return runners;

    }


    @Override
    protected void onPostExecute(List<Runner> result) {

        super.onPostExecute(result);

    }
}
