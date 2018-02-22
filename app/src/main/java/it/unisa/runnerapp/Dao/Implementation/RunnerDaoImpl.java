package it.unisa.runnerapp.Dao.Implementation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.runnerapp.Dao.Interf.RunnerDao;
import it.unisa.runnerapp.beans.Runner;
import it.unisa.runnerapp.utils.ConnectionUtil;

/**
 * Created by Paolo on 27/01/2018.
 */

public class RunnerDaoImpl implements RunnerDao {

    public RunnerDaoImpl(){}



    @Override
    public void createRunner(final Runner runner) {

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    try {

                        String sql = "INSERT INTO Utenti "
                                + " (nickname,password,nome,cognome,data_nascita,peso,livello,img_profilo)" +
                                "    VALUES (?, ?, ?,?,?,?,?,?)";

                        ps = ConnectionUtil.getConnection().prepareStatement(sql);

                        long mills = new java.util.Date().getTime();
                        byte[] byteArray =null;

                        if(runner.getProfileImage()!=null) {
                            Bitmap bitMap = drawableToBitmap(runner.getProfileImage());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();
                        }


                            ps.setString(1,runner.getNickname());
                            ps.setString(2,runner.getPassword());
                            ps.setString(3,runner.getName());
                            ps.setString(4,runner.getSurname());
                            ps.setDate(5, new Date(runner.getBirthDare().getTime()));
                            ps.setDouble(6,runner.getWeight());
                            ps.setShort(7,(short) runner.getLevel());
                            ps.setBytes(8,byteArray);


                            int result = ps.executeUpdate();




                    }

                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }

                    return null;


                }

                public Bitmap drawableToBitmap(Drawable drawable) {
                    if (drawable instanceof BitmapDrawable) {
                        return ((BitmapDrawable) drawable).getBitmap();
                    }

                    final int width = !drawable.getBounds().isEmpty() ? drawable
                            .getBounds().width() : drawable.getIntrinsicWidth();

                    final int height = !drawable.getBounds().isEmpty() ? drawable
                            .getBounds().height() : drawable.getIntrinsicHeight();

                    final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width,
                            height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);

                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);

                    return bitmap;
                }


            }.execute().get();
        }

        catch (Exception e) {
            Log.e("Exception",Log.getStackTraceString(e));
        }


    }

    @Override
    public void updateRunner(final Runner runner) {

        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                        PreparedStatement ps = null;
                        String sql = "UPDATE Utenti SET Utenti.password ='" + runner.getPassword() + "', Utenti.nome='" + runner.getName() + "', Utenti.cognome='" + runner.getSurname() + "', Utenti.peso=" + runner.getWeight() + ", Utenti.livello=" + runner.getLevel() + ", Utenti.km_percorsi = " + runner.getTraveledKilometers() + ", Utenti.data_nascita= '" + new Date(runner.getBirthDare().getTime()) + "' WHERE Utenti.nickname = '" + runner.getNickname() + "'";

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
    public void deleteRunner(final int nickuser) {

        try {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    PreparedStatement ps = null;
                    String sql = "DELETE FROM Utenti WHERE Utenti.nickname ='" + nickuser + "'";

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
    public Runner getByNick(final String nickname) {

        try {

          return  new AsyncTask<Void, Void, Runner>() {
                @Override
                protected Runner doInBackground( final Void ... params ) {
                    ResultSet rs =null;
                    PreparedStatement ps = null;
                    Runner runner = null;
                    try {

                        ps = ConnectionUtil.getConnection().prepareStatement("select * from Utenti WHERE Utenti.nickname = '" + nickname + "'");
                        rs = ps.executeQuery();


                        rs.next();


                            runner = new Runner();

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


                    }


                    catch (SQLException e) {
                        Log.e("SQLException",Log.getStackTraceString(e));
                    }
                    return runner;
                }

                @Override
                protected void onPostExecute( Runner result ) {
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
    public List<Runner> getAllRunners() {

        List<Runner> runners = null;
        try {

            runners = new AsyncTask<Void, Void, List<Runner>>() {
                   @Override
                   protected List<Runner> doInBackground( final Void ... params ) {
                       ResultSet rs =null;
                       List<Runner> runners = new ArrayList<Runner>();
                       PreparedStatement ps = null;
                       try {

                           ps = ConnectionUtil.getConnection().prepareStatement("select * from Utenti");
                           rs = ps.executeQuery();


                           while (rs.next()) {


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
            e.printStackTrace();
        }

        return runners;
    }
}
