package it.unisa.runnerapp.Dao.Implementation;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.Dao.Interf.FinishedRanDao;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.FinishedRun;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.ConnectionUtil;

/**
 * Created by Paolo on 27/01/2018.
 */

public class FinishedRanDaoImpl implements FinishedRanDao{


    @Override
    public void createFinishedRun(final FinishedRun finishedrun) {

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;

                    try {

                        String sql = "INSERT INTO Corse_Terminate "
                                + " (corsa, partecipante, km_percorsi, calorie_bruciate,velocita_media)" +
                                "    VALUES (?, ?, ?, ?, ?)";

                        ps = ConnectionUtil.getConnection().prepareStatement(sql);


                        ps.setInt(1, finishedrun.getId());
                        ps.setString(2,finishedrun.getRunner().getNickname());
                        ps.setDouble(3,finishedrun.getTraveledKm());
                        ps.setDouble(4,finishedrun.getBurnedCal());
                        ps.setDouble(5,finishedrun.getAverageSpeed());

                        ps.executeUpdate();

                    }

                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }

                    return null;


                }



            }.execute().get();
        }

        catch (Exception e) {
            Log.e("Exception",Log.getStackTraceString(e));
        }




    }


    @Override
    public void deleteFinishedRun(final int idfinishedrun) {

        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "DELETE FROM Corse_Terminate WHERE Corse_Terminate.corsa =" + idfinishedrun;

                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement(sql);
                        int result = ps.executeUpdate();

                    }

                    catch (SQLException e) {
                        Log.e("Exception",Log.getStackTraceString(e));
                    }
                    return null;
                }



            }.execute().get();

        }

        catch (Exception e) {
            Log.e("Exception",Log.getStackTraceString(e));
        }
    }

    @Override
    public List<FinishedRun> getAllFinishedRuns() {

        try {

            return  new AsyncTask<Void, Void, List<FinishedRun>>() {
                @Override
                protected List<FinishedRun> doInBackground( final Void ... params ) {
                    ResultSet rs =null;
                    PreparedStatement ps = null;
                    List<FinishedRun> finishedruns = new ArrayList<FinishedRun>();
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Corse_Terminate join Corse on Corse_Terminate.corsa = Corse.id join Utenti on Utenti.nickname = Corse_Terminate.partecipante");
                        rs = ps.executeQuery();

                        while(rs.next()) {


                            FinishedRun run = new FinishedRun();

                            run.setId(rs.getInt("id"));
                            LatLng latLng = new LatLng(rs.getDouble("punto_ritrovo_lat"), rs.getDouble("punto_ritrovo_lng"));
                            run.setMeetingPoint(latLng);
                            run.setStartDate(rs.getDate("data_inizio"));


                            String idmaster = rs.getString("master");

                            Runner runner = new Runner();
                            runner.setNickname(rs.getString("nickname"));
                            runner.setPassword(rs.getString("password"));
                            runner.setName(rs.getString("nome"));
                            runner.setSurname(rs.getString("cognome"));
                            runner.setBirthDate(rs.getDate("data_nascita"));
                            runner.setWeight(rs.getDouble("peso"));
                            runner.setLevel(rs.getShort("livello"));
                            runner.setTraveledKilometers(rs.getDouble("km_percorsi"));



                            byte[] bytes_imgprofilo = rs.getBytes("img_profilo");

                            if (bytes_imgprofilo != null) {

                                runner.setProfileImage(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes_imgprofilo, 0, bytes_imgprofilo.length)));

                            }

                            run.setRunner(runner);


                            run.setTraveledKm(rs.getDouble("km_percorsi"));
                            run.setBurnedCal(rs.getInt("calorie_bruciate"));
                            run.setAverageSpeed(rs.getInt("velocita_media"));

                            finishedruns.add(run);

                        }
                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return finishedruns;
                }

                @Override
                protected void onPostExecute( List<FinishedRun> result ) {
                    super.onPostExecute(result);
                }
            }.execute().get();
        }


        catch (Exception e) {
            Log.e("Exception",Log.getStackTraceString(e));
        }

        return null;
    }

    @Override
    public FinishedRun findByID(final int idfinishedrun) {

        try {

            return  new AsyncTask<Void, Void, FinishedRun>() {
                @Override
                protected FinishedRun doInBackground( final Void ... params ) {
                    ResultSet rs =null;
                    ResultSet rsmaster = null;
                    PreparedStatement ps = null;
                    FinishedRun run = null;
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Corse_Terminate join Corse on Corse_Terminate.corsa = Corse.id join Utenti on Utenti.nickname = Corse_Terminate.partecipante WHERE Corse_Terminate.corsa = " + idfinishedrun);
                        rs = ps.executeQuery();


                        rs.next();


                        run = new FinishedRun();

                        run.setId(rs.getInt("id"));
                        LatLng latLng = new LatLng(rs.getDouble("punto_ritrovo_lat"),rs.getDouble("punto_ritrovo_lng"));
                        run.setMeetingPoint(latLng);
                        run.setStartDate(rs.getDate("data_inizio"));

                        String idmaster = rs.getString("master");

                        Runner runner = new Runner();
                        runner.setNickname(rs.getString("nickname"));
                        runner.setPassword(rs.getString("password"));
                        runner.setName(rs.getString("nome"));
                        runner.setSurname(rs.getString("cognome"));
                        runner.setBirthDate(rs.getDate("data_nascita"));
                        runner.setWeight(rs.getDouble("peso"));
                        runner.setLevel(rs.getShort("livello"));
                        runner.setTraveledKilometers(rs.getDouble("km_percorsi"));

                        byte[] bytes_imgprofilo =  rs.getBytes("img_profilo");

                        if(bytes_imgprofilo!=null){

                            runner.setProfileImage(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes_imgprofilo, 0, bytes_imgprofilo.length)));

                        }


                        run.setRunner(runner);

                        run.setTraveledKm(rs.getDouble("km_percorsi"));
                        run.setBurnedCal(rs.getInt("calorie_bruciate"));
                        run.setAverageSpeed(rs.getInt("velocita_media"));


                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return run;
                }

                @Override
                protected void onPostExecute( FinishedRun result ) {
                    super.onPostExecute(result);
                }
            }.execute().get();
        }
        catch (Exception e) {
            Log.e("Exception",Log.getStackTraceString(e));
        }

        return null;

    }
}
