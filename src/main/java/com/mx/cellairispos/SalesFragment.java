package com.mx.cellairispos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class SalesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflates the layout with the containers for the fragments
        View view = inflater.inflate(R.layout.containers, container, false);

        //Starts a transaction and adds ProductsFragment to the right on grid mode and SalesListFragment to the left
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_container, new SalesListFragment(), SalesListFragment.TAG);
        transaction.add(R.id.right_container, ProductsFragment.newInstance(true), ProductsFragment.TAG);

        //Finishes the transaction
        transaction.commit();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Changes the ActionBar title when the fragment is attached
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.SALES);
    }
}
