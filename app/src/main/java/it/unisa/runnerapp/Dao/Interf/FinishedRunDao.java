package it.unisa.runnerapp.Dao.Interf;

import java.util.List;

import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.FinishedRun;

/**
 * Created by Paolo on 27/01/2018.
 */

public interface FinishedRunDao {



    public void createFinishedRun(FinishedRun finishedrun);
    public void deleteFinishedRun(int idfinishedrun);
    public List<FinishedRun> getAllFinishedRuns();
    public FinishedRun findByID(int idfinishedrun);

    // Recupera tutte le gare terminate inserite da un determinato utente senza caricare le sue informazioni personali
    public List<FinishedRun> findByRunnerWithoutMaster(String nickname, String order);
}
