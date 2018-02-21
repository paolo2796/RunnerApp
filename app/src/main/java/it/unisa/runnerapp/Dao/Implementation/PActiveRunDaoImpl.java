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

import it.unisa.runnerapp.Dao.Interf.PActiveRunDao;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.ConnectionUtil;

/**
 * Created by Paolo on 27/01/2018.
 */

public class PActiveRunDaoImpl implements PActiveRunDao {

    @Override
    public void createParticipationRun(final int idrun, final String nickrunner) {

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;

                    try {

                        String sql = "INSERT INTO Partecipazioni_Corse_Attive "
                                + " (corsa, partecipante)" +
                                "    VALUES (?, ?)";

                        ps = ConnectionUtil.getConnection().prepareStatement(sql);


                        ps.setInt(1,idrun);
                        ps.setString(2,nickrunner);

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
    public void deleteParticipationRun(final int idrun, final String nickrunner) {

        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "DELETE FROM Partecipazioni_Corse_Attive WHERE Partecipazioni_Corse_Attive.corsa =" + idrun + " and Partecipazioni_Corse_Attive.partecipante='" + nickrunner + "'";

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
    public void updateParticipationRun(final int idrun, final String nickrunner) {


        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "UPDATE Partecipazioni_Corse_Attive pca SET pca.corsa =" + idrun + ", pca.partecipante='" + nickrunner + "'" ;

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
    public List<ActiveRun> findRunByRunner(final String nickuser, final String order) {

        try {

            return  new AsyncTask<Void, Void, List<ActiveRun>>() {
                @Override
                protected List<ActiveRun> doInBackground( final Void ... params ) {
                    ResultSet rs =null;

                    PreparedStatement ps = null;
                    List<ActiveRun> activeruns = new ArrayList<ActiveRun>();
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Partecipazioni_Corse_Attive pca join Corse_Attive ca on pca.corsa = ca.corsa join Corse on Corse.id = ca.corsa where pca.partecipante = ? ORDER BY ?");
                        ps.setString(1,nickuser);
                        ps.setString(2,order);
                        rs = ps.executeQuery();


                        while(rs.next()) {

                            ActiveRun run = new ActiveRun();
                            run.setId(rs.getInt("id"));
                            LatLng latLng = new LatLng(rs.getDouble("punto_ritrovo_lat"), rs.getDouble("punto_ritrovo_lng"));
                            run.setMeetingPoint(latLng);
                            run.setStartDate(rs.getTimestamp("data_inizio"));
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
    public List<ActiveRun> findRunActiveByRunner(final String nickuser, final String order) {

        try {

            return  new AsyncTask<Void, Void, List<ActiveRun>>() {
                @Override
                protected List<ActiveRun> doInBackground( final Void ... params ) {
                    ResultSet rs =null;

                    PreparedStatement ps = null;
                    List<ActiveRun> activeruns = new ArrayList<ActiveRun>();
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Partecipazioni_Corse_Attive pca join Corse_Attive ca on pca.corsa = ca.corsa join Corse on Corse.id = ca.corsa where (timestampdiff(HOUR,current_timestamp(),data_inizio))>-1 and  pca.partecipante = ? ORDER BY " + order);
                        ps.setString(1,nickuser);
                        rs = ps.executeQuery();


                        while(rs.next()) {

                            ActiveRun run = new ActiveRun();
                            run.setId(rs.getInt("id"));
                            LatLng latLng = new LatLng(rs.getDouble("punto_ritrovo_lat"), rs.getDouble("punto_ritrovo_lng"));
                            run.setMeetingPoint(latLng);
                            run.setStartDate(rs.getTimestamp("data_inizio"));
                            run.setEstimatedKm(rs.getDouble("km_previsti"));
                            run.setEstimatedHours(rs.getInt("ore_previste"));
                            run.setEstimatedMinutes(rs.getInt("minuti_previsti"));

                            Runner master = new Runner();
                            master.setNickname(rs.getString("Corse.master"));
                            run.setMaster(master);

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
    public List<Run> findRunByRunnerFetchID(final String nickuser, final String order) {

        try {
            return  new AsyncTask<Void, Void, List<Run>>() {
                @Override
                protected List<Run> doInBackground( final Void ... params ) {
                    ResultSet rs =null;

                    PreparedStatement ps = null;
                    List<Run> activeruns = new ArrayList<Run>();
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select Corse.id from Partecipazioni_Corse_Attive pca join Corse_Attive ca on pca.corsa = ca.corsa join Corse on Corse.id = ca.corsa join Utenti on Utenti.nickname = Corse.master where pca.partecipante = '"  + nickuser +  "' ORDER BY " + order);
                        rs = ps.executeQuery();

                        while(rs.next()) {

                            Run run = new Run();
                            run.setId(rs.getInt("id"));
                            activeruns.add(run);

                        }
                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return activeruns;
                }

                @Override
                protected void onPostExecute( List<Run> result ) {
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
    public List<Runner> findRunnerByRun(final int idactiverun) {

        try {

            return  new AsyncTask<Void, Void, List<Runner>>() {
                @Override
                protected List<Runner> doInBackground( final Void ... params ) {
                    ResultSet rs =null;

                    PreparedStatement ps = null;
                    List<Runner> runners = new ArrayList<Runner>();
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Partecipazioni_Corse_Attive pca join Utenti on Utenti.nickname = pca.partecipante where pca.corsa = '"  + idactiverun +  "'");
                        rs = ps.executeQuery();

                        while(rs.next()) {

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

                            runners.add(runner);


                        }
                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return runners;
                }

                @Override
                protected void onPostExecute( List<Runner> result ) {
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
    public void deleteAllRunnerByRun(final int codrun) {

        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "DELETE FROM Partecipazioni_Corse_Attive WHERE Partecipazioni_Corse_Attive.corsa =" + codrun;

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
}
