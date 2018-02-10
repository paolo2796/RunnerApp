package it.unisa.runnerapp.Dao.Interf;

import java.util.List;

import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.Run;
import it.unisa.runnerapp.beans.Runner;

/**
 * Created by Paolo on 27/01/2018.
 */

public interface PActiveRunDao {

    public void createParticipationRun(int idrun, String nickrunner);
    public void deleteParticipationRun(int idrun, String nickrunner);
    public void updateParticipationRun(int idrun, String nickrunner);

    // Ricerca tutte le corse attive di un runner (senza master)
    public List<ActiveRun> findRunByRunner(String nickuser, String order);

    // Ricerca tutte le corse attive di un runner (solo id).
    public List<Run> findRunByRunnerFetchID(String nickuser);

    // Ricerca i runner che partecipano ad una corsa attiva.
    public List<Runner> findRunnerByRun(int idactiverun);

}
