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

import it.unisa.runnerapp.Dao.Interf.UnionRunDao;
import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.ConnectionUtil;

/**
 * Created by Paolo on 27/01/2018.
 */

public class UnionRunDaoImpl implements UnionRunDao {


    @Override
    public void createUnion(final int idhost, final int idhosted) {

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;

                    try {

                        String sql = "INSERT INTO Unioni_Corse "
                                + " (ospitante, ospitato)" +
                                "    VALUES (?, ?)";

                        ps = ConnectionUtil.getConnection().prepareStatement(sql);


                        ps.setInt(1,idhost);
                        ps.setInt(2,idhosted);

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
    public void updateUnion(final int idhost, final int idhosted) {

        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "UPDATE Unioni_Corse  SET Unioni_Corse.ospitante =" + idhost + ", Unioni_Corse.ospitato=" + idhosted  ;

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
    public List<Run> findHostByID(final int idhost) {

        try {

            return  new AsyncTask<Void, Void, List<Run>>() {
                @Override
                protected List<Run> doInBackground( final Void ... params ) {
                    ResultSet rs =null;

                    PreparedStatement ps = null;
                    List<Run> runs = new ArrayList<Run>();
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Unioni_Corse uc join Corse on uc.ospitato = Corse.id join Utenti on Utenti.nickname = Corse.master where uc.ospitante = "  + idhost);
                        rs = ps.executeQuery();

                        while(rs.next()) {


                            ActiveRun run = new ActiveRun();

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

                            run.setMaster(runner);



                            run.setEstimatedKm(rs.getDouble("km_previsti"));
                            run.setEstimatedHours(rs.getInt("ore_previste"));
                            run.setEstimatedMinutes(rs.getInt("minuti_previsti"));

                            runs.add(run);

                        }
                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return runs;
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
}
