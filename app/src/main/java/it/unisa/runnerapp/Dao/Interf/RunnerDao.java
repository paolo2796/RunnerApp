package it.unisa.runnerapp.Dao.Interf;

import java.util.List;

import it.unisa.runnerapp.beans.Runner;

/**
 * Created by Paolo on 27/01/2018.
 */

public interface RunnerDao {

    public List<Runner> getAllRunners();
    public void createRunner(Runner runner);
    public void updateRunner(Runner runner);
    public void deleteRunner(int nickuser);
    public Runner getByNick(String nickname);
}
