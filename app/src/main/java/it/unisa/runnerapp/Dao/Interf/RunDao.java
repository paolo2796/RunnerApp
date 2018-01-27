package it.unisa.runnerapp.Dao.Interf;

import java.util.List;

import it.unisa.runnerapp.beans.Run;

/**
 * Created by Paolo on 27/01/2018.
 */

public interface RunDao {


    public void createRun(Run run);
    public void updateRun(Run run);
    public void deleteRun(int idrun);
    public Run findByID(int idrun);
    public List<Run> getAllRuns();


}
