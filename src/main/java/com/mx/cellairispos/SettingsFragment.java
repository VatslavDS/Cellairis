package com.mx.cellairispos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import util.HandleLogout;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        //Adding preferences for general

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflates the layout with the containers for the fragments
        View view = inflater.inflate(R.layout.containers, container, false);
        FragmentManager fragmentManager;
        fragmentManager = getFragmentManager();
        onLogoutDialog(fragmentManager);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Changes the ActionBar title when the fragment is attached
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.SETTINGS);
    }

    private void onLogoutDialog(final FragmentManager fragmentManager){

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.logout, null);
        new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.no_buttton_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = new SalesFragment();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.replace(R.id.container, fragment, fragment.getTag()).commit();
                    }
                })
                .setPositiveButton(R.string.yes_button_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //We need to clean session and start a new login session
                        /*
                        *
                        *               CLEAN THE SESSION
                        *
                         */
                        HandleLogout.noProfile(getActivity());
                        HandleLogout.noSession(getActivity());
                        HandleLogout.noToken(getActivity());
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                })
                .setView(view)
                .show();

    }
}
