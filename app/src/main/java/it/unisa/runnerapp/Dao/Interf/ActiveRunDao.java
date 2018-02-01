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
    public ActiveRun findByID(int idrun);

    // Recupera tutte le gare non ancora iniziate che si terranno nella giornata odiera
    public List<ActiveRun> getActiveRunsWithinDay();
}
