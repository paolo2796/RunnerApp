package it.unisa.runnerapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.unisa.runnerapp.adapters.LiveRequestsAdapter;
import testapp.com.runnerapp.R;

public class ReceivedRequestsListFragment extends Fragment
{
    private ListView             receivedRequestsList;
    private LiveRequestsAdapter  requestsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.received_requests_fragment,container,false);

        receivedRequestsList=v.findViewById(R.id.receivedRequestsList);
        receivedRequestsList.setAdapter(requestsAdapter);

        return v;
    }

    public void setRequestsAdapter(LiveRequestsAdapter requestsAdapter)
    {
        this.requestsAdapter=requestsAdapter;
    }

}
