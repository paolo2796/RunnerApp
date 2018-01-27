package it.unisa.runnerapp.Dao.Interf;

import java.util.List;

import it.unisa.runnerapp.beans.Run;

/**
 * Created by Paolo on 27/01/2018.
 */

public interface UnionRunDao {


    public void createUnion(int idhost, int idhosted);
    public void updateUnion(int idhost, int idhosted);
    public List<Run> findHostByID(int idhost);


}
