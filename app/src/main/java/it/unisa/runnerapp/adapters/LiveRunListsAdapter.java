package it.unisa.runnerapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.unisa.runnerapp.beans.LiveRequest;
import it.unisa.runnerapp.fragments.AcceptedRequestsListFragment;
import it.unisa.runnerapp.fragments.ReceivedRequestsListFragment;

public class LiveRunListsAdapter extends FragmentPagerAdapter
{
    private ReceivedRequestsListFragment receivedRequestsFragment;
    private AcceptedRequestsListFragment acceptedRequestsFragment;

    private static final String[] TABS_TITLES={"In Arrivo","Accettate"};
    private static final int TABS_NUM=TABS_TITLES.length;

    public LiveRunListsAdapter(FragmentManager fm)
    {
        super(fm);
        receivedRequestsFragment=new ReceivedRequestsListFragment();
        acceptedRequestsFragment=new AcceptedRequestsListFragment();
    }
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return receivedRequestsFragment;
            case 1:
                return acceptedRequestsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TABS_NUM;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return  TABS_TITLES[position];
    }

    public ReceivedRequestsListFragment getReceivedRequestsFragment()
    {
        return receivedRequestsFragment;
    }

    public AcceptedRequestsListFragment getAcceptedRequestsFragment()
    {
        return acceptedRequestsFragment;
    }
}
