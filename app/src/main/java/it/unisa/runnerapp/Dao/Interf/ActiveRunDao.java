package it.unisa.runnerapp.Dao.Interf;

import java.util.List;

import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;

/**
 * Created by Paolo on 27/01/2018.
 */

public interface ActiveRunDao {


    public void createActiveRun(ActiveRun activerun);
    public void updateActiveRun(ActiveRun activerun);
    public void deleteActiveRun(int idactiverun);
    public List<ActiveRun> getAllActiveRuns();

    // Recupera una determinata corsa (recupera solamente l'id del master)
    public ActiveRun findByID(int idrun);


    // Recupera tutte le gare inserite da un determinato utente
    public List<ActiveRun> findByRunner(String nickname);

    // Recupera tutte le gare disponibili per un determinato utente che si terranno entro 24 ore (senza master)
    public List<ActiveRun> getAvailableRunsWithin24hByRunner(String nickname, String orderby);

    // Recupera tutte le gare disponibili per un determinato utente (senza master)
    public List<ActiveRun> getAvailableByRunner(String nickname, String orderby);


    public List<ActiveRun> findByRunnerWithin24hWithoutMaster(String nickname, String order);


}
