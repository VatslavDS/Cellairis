package com.mx.cellairispos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import database.models.Customer;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class ClientsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflates the layout with the containers for the fragments
        View view = inflater.inflate(R.layout.containers, container, false);

        //Starts a transaction and adds ClientsListFragment on the left
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_container, new ClientsListFragment(), ClientsListFragment.TAG);

        //Reads the DB and adds ClientsAddEditFragment or ClientsDetailFragment depending on what was read.
        List<Customer> customers = Customer.getAll(getActivity());
        if (customers.isEmpty())
            transaction.add(R.id.right_container, new ClientsAddEditFragment(), ClientsAddEditFragment.TAG);
        else
            transaction.add(R.id.right_container, ClientsDetailFragment.newInstance(customers.get(0).getId()), ClientsDetailFragment.TAG);

        //Finishes the transaction
        transaction.commit();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Changes the ActionBar title when the fragment is attached
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.CLIENTS);
    }
}
