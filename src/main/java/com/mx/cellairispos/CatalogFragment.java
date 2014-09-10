package com.mx.cellairispos;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import database.models.Product;
import util.HandleSession;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class CatalogFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflates the layout with the containers for the fragments
        View view = inflater.inflate(R.layout.containers, container, false);

        //Starts a transaction and adds ProductsFragment on the left in list mode
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_container, ProductsFragment.newInstance(false), ProductsFragment.TAG);

        //Reads the DB and adds ProductsDetailFragment empty or showing the first product depending on what was read.
        List<Product> products = Product.getAll(getActivity());
        if (products.isEmpty())
            transaction.add(R.id.right_container, new ProductsDetailFragment(), ProductsDetailFragment.TAG);
        else
            transaction.add(R.id.right_container, ProductsDetailFragment.newInstance(products.get(0).getId(), true), ProductsDetailFragment.TAG);

        //Finishes the transaction
        transaction.commit();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Changes the ActionBar title when the fragment is attached
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.CATALOG);
    }
}
