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

    // Recupera una determinata corsa senza recuperare l'immagine del profilo del master
    public ActiveRun findByID(int idrun);

    // Recupera tutte le gare non ancora iniziate che si terranno entro 24 ore
    public List<ActiveRun> getActiveRunsWithin24h(String orderby);


    // Recupera tutte le gare inserite da un determinato utente
    public List<ActiveRun> findByRunner(String nickname);

    // Recupera tutte le gare non ancora iniziate che si terranno entro 24 ore (senza master)
    public List<ActiveRun> getActiveRunsWithin24hWithoutMaster(String orderby);
}
