package com.mx.cellairispos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class PaymentHistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflates the layout with the containers for the fragments
        View view = inflater.inflate(R.layout.containers, container, false);

        //Starts a transaction and adds PaymentListFragment on the left and PaymentDetailFragment on the right
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_container, new PaymentListFragment(), PaymentListFragment.TAG);
        transaction.add(R.id.right_container, new PaymentDetailFragment(), PaymentDetailFragment.TAG);

        //Finishes the transaction
        transaction.commit();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Changes the ActionBar title when the fragment is attached
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.PAYMENTS_HISTORY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Notifies the system that this fragment wants to participate on the menu creation
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //Clears the options menu
        menu.clear();
    }
}
