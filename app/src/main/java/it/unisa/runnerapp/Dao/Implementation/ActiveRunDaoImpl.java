package it.unisa.runnerapp.Dao.Implementation;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.Dao.Interf.ActiveRunDao;
import it.unisa.runnerapp.Dao.Interf.RunDao;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.ConnectionUtil;

/**
 * Created by Paolo on 27/01/2018.
 */

public class ActiveRunDaoImpl implements ActiveRunDao {


    @Override
    public void createActiveRun(final ActiveRun activerun) {

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;

                    try {

                        String sql = "INSERT INTO Corse_Attive "
                                + " (corsa, km_previsti, ore_previste, minuti_previsti)" +
                                "    VALUES (?, ?, ?,?)";

                        ps = ConnectionUtil.getConnection().prepareStatement(sql);


                        ps.setInt(1,activerun.getId());
                        ps.setDouble(2,activerun.getEstimatedKm());
                        ps.setInt(3,activerun.getEstimatedHours());
                        ps.setInt(4,activerun.getEstimatedMinutes());

                        int result = ps.executeUpdate();

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
    public void updateActiveRun(final ActiveRun activerun) {



        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "UPDATE Corse_Attive SET Corse_Attive.km_previsti ='" + activerun.getEstimatedKm() + "', Corse_Attive.ore_previste='" + activerun.getEstimatedHours() + "', Corse_Attive.minuti_previsti='" + activerun.getEstimatedMinutes();

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
    public void deleteActiveRun(final int idactiverun) {



        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "DELETE FROM Corse_Attive WHERE Corse_Attive.corsa =" + idactiverun;

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
    public List<ActiveRun> getAllActiveRuns() {

        try {

            return  new AsyncTask<Void, Void, List<ActiveRun>>() {
                @Override
                protected List<ActiveRun> doInBackground( final Void ... params ) {
                    ResultSet rs =null;
                    ResultSet rsmaster =null;

                    PreparedStatement ps = null;
                    List<ActiveRun> activeruns = new ArrayList<ActiveRun>();
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Corse_Attive join Corse on Corse_Attive.corsa = Corse.id ");
                        rs = ps.executeQuery();

                        while(rs.next()) {


                            ActiveRun run = new ActiveRun();

                            run.setId(rs.getInt("id"));
                            LatLng latLng = new LatLng(rs.getDouble("punto_ritrovo_lat"), rs.getDouble("punto_ritrovo_lng"));
                            run.setMeetingPoint(latLng);
                            run.setStartDate(rs.getDate("data_inizio"));


                            String idmaster = rs.getString("master");

                            rsmaster = ConnectionUtil.getConnection().prepareStatement("select * from Utenti where Utenti.nickname = '" + idmaster + "'").executeQuery();
                            Runner runner = new Runner();
                            rsmaster.next();
                            runner.setNickname(rsmaster.getString("nickname"));
                            runner.setPassword(rsmaster.getString("password"));
                            runner.setName(rsmaster.getString("nome"));
                            runner.setSurname(rsmaster.getString("cognome"));
                            runner.setBirthDate(rsmaster.getDate("data_nascita"));
                            runner.setWeight(rsmaster.getDouble("peso"));
                            runner.setLevel(rsmaster.getShort("livello"));
                            runner.setTraveledKilometers(rsmaster.getDouble("km_percorsi"));



                            byte[] bytes_imgprofilo = rsmaster.getBytes("img_profilo");

                            if (bytes_imgprofilo != null) {

                                runner.setProfileImage(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes_imgprofilo, 0, bytes_imgprofilo.length)));

                            }

                            run.setMaster(runner);



                            run.setEstimatedKm(rs.getDouble("km_previsti"));
                            run.setEstimatedHours(rs.getInt("ore_previste"));
                            run.setEstimatedMinutes(rs.getInt("minuti_previsti"));

                            activeruns.add(run);

                        }
                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return activeruns;
                }

                @Override
                protected void onPostExecute( List<ActiveRun> result ) {
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
    public ActiveRun findByID(final int idrun) {


        try {

            return  new AsyncTask<Void, Void, ActiveRun>() {
                @Override
                protected ActiveRun doInBackground( final Void ... params ) {
                    ResultSet rs =null;
                    ResultSet rsmaster = null;
                    PreparedStatement ps = null;
                    ActiveRun run = null;
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Corse_Attive join Corse on Corse_Attive.corsa = Corse.id WHERE Corse_Attive.corsa = " + idrun + "");
                        rs = ps.executeQuery();


                        rs.next();


                        run = new ActiveRun();

                        run.setId(rs.getInt("id"));
                        LatLng latLng = new LatLng(rs.getDouble("punto_ritrovo_lat"),rs.getDouble("punto_ritrovo_lng"));
                        run.setMeetingPoint(latLng);
                        run.setStartDate(rs.getDate("data_inizio"));

                        String idmaster = rs.getString("master");

                        rsmaster = ConnectionUtil.getConnection().prepareStatement("select * from Utenti where Utenti.nickname = '" + idmaster + "'").executeQuery();
                        Runner runner = new Runner();
                        rsmaster.next();
                        runner.setNickname(rsmaster.getString("nickname"));
                        runner.setPassword(rsmaster.getString("password"));
                        runner.setName(rsmaster.getString("nome"));
                        runner.setSurname(rsmaster.getString("cognome"));
                        runner.setBirthDate(rsmaster.getDate("data_nascita"));
                        runner.setWeight(rsmaster.getDouble("peso"));
                        runner.setLevel(rsmaster.getShort("livello"));
                        runner.setTraveledKilometers(rsmaster.getDouble("km_percorsi"));

                        byte[] bytes_imgprofilo =  rsmaster.getBytes("img_profilo");

                        if(bytes_imgprofilo!=null){

                            runner.setProfileImage(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes_imgprofilo, 0, bytes_imgprofilo.length)));

                        }


                        run.setMaster(runner);

                        run.setEstimatedKm(rs.getDouble("km_previsti"));
                        run.setEstimatedHours(rs.getInt("ore_previste"));
                        run.setEstimatedMinutes(rs.getInt("minuti_previsti"));


                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return run;
                }

                @Override
                protected void onPostExecute( ActiveRun result ) {
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
