package it.unisa.runnerapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.unisa.runnerapp.adapters.AcceptedRequestsAdapter;
import testapp.com.runnerapp.R;

public class AcceptedRequestsListFragment extends Fragment
{
    private ListView                acceptedRequests;
    private AcceptedRequestsAdapter acceptedRequestsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.accepted_requests_fragment,container,false);

        acceptedRequests=v.findViewById(R.id.acceptedRequestsList);
        acceptedRequests.setAdapter(acceptedRequestsAdapter);

        return v;
    }

    public void setAcceptedRequestsAdapter(AcceptedRequestsAdapter acceptedRequestsAdapter)
    {
        this.acceptedRequestsAdapter=acceptedRequestsAdapter;
    }
}
