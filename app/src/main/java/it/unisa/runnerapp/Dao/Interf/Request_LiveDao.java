package it.unisa.runnerapp.Dao.Interf;

import java.util.List;

import it.unisa.runnerapp.beans.ActiveRun;
import it.unisa.runnerapp.beans.RequestLive;

/**
 * Created by Paolo on 09/02/2018.
 */

public interface Request_LiveDao {

    public void createRequestLive(RequestLive requestlive);
    public void updateRequestLive(String nickapplicant, String nickrecipient);
    public void deleteRequestLive(int idrequestlive);
    public List<RequestLive> findByRunnerRecipient(String nickrecipient);
}
