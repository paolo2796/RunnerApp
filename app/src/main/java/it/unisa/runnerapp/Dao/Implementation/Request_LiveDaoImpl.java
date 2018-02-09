package it.unisa.runnerapp.Dao.Implementation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.Dao.Interf.Request_LiveDao;
import it.unisa.runnerapp.beans.RequestLive;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.ConnectionUtil;

/**
 * Created by Paolo on 09/02/2018.
 */

public class Request_LiveDaoImpl implements Request_LiveDao {

    public Request_LiveDaoImpl(){}


    @Override
    public void createRequestLive(final RequestLive requestlive) {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    try {

                        String sql = "INSERT INTO Request_Live "
                                + " (user_applicant,user_recipient, latitude, longitude)" +
                                "    VALUES (?, ?, ?, ?)";

                        ps = ConnectionUtil.getConnection().prepareStatement(sql);

                        ps.setString(1,requestlive.getRunner_applicant().getNickname());
                        ps.setString(2, requestlive.getRunner_recipient().getNickname());
                        ps.setDouble(3,requestlive.getWaypoint().latitude);
                        ps.setDouble(4,requestlive.getWaypoint().longitude);

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
    public void updateRequestLive(String nickapplicant, String nickrecipient) {}

    @Override
    public void deleteRequestLive(final int idrequestlive) {

        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "DELETE FROM Request_Live WHERE Request_Live.id =" + idrequestlive + "";

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
            e.printStackTrace();
        }

    }

    @Override
    public List<RequestLive> findByRunnerRecipient(final String nickrecipient) {

        List<RequestLive> requests = null;
        try {

            requests = new AsyncTask<Void, Void, List<RequestLive>>() {
                @Override
                protected List<RequestLive> doInBackground( final Void ... params ) {
                    ResultSet rs =null;
                    List<RequestLive> requestslive = new ArrayList<RequestLive>();
                    PreparedStatement ps = null;
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Request_Live rl join Runner rapplicant on rapplicant.nickname = rl.user_applicant join Runner rrecipient on rrecipient.nickname = rl.user_recipient where rapplicant = ?  ");
                        ps.setString(1,nickrecipient);
                        rs = ps.executeQuery();

                        while (rs.next()) {


                            RequestLive requestlive = null;


                            Runner runnerapplicant = new Runner();

                            runnerapplicant.setNickname(rs.getString("rapplicant.nickname"));
                            runnerapplicant.setPassword(rs.getString("rapplicant.password"));
                            runnerapplicant.setName(rs.getString("rapplicant.nome"));
                            runnerapplicant.setSurname(rs.getString("rapplicant.cognome"));
                            runnerapplicant.setBirthDate(rs.getDate("rapplicant.data_nascita"));
                            runnerapplicant.setWeight(rs.getDouble("rapplicant.peso"));
                            runnerapplicant.setLevel(rs.getShort("rapplicant.livello"));
                            runnerapplicant.setTraveledKilometers(rs.getDouble("rapplicant.km_percorsi"));

                            Runner runnerecipient = new Runner();

                            runnerapplicant.setNickname(rs.getString("rrecipient.nickname"));
                            runnerapplicant.setPassword(rs.getString("rrecipient.password"));
                            runnerapplicant.setName(rs.getString("rrecipient.nome"));
                            runnerapplicant.setSurname(rs.getString("rrecipient.cognome"));
                            runnerapplicant.setBirthDate(rs.getDate("rrecipient.data_nascita"));
                            runnerapplicant.setWeight(rs.getDouble("rrecipient.peso"));
                            runnerapplicant.setLevel(rs.getShort("rrecipient.livello"));
                            runnerapplicant.setTraveledKilometers(rs.getDouble("rrecipient.km_percorsi"));

                            LatLng waypoint = new LatLng(rs.getDouble("rl.latitude"),rs.getDouble("rl.longitude"));




                            requestlive = new RequestLive(rs.getInt("rl.id"),runnerapplicant,runnerecipient,waypoint);

                            requestslive.add(requestlive);

                        }

                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return requestslive;
                }

                @Override
                protected void onPostExecute( List<RequestLive> result ) {
                    super.onPostExecute(result);
                }
            }.execute().get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return requests;



    }
}
