package com.mx.cellairispos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import database.models.Customer;
import util.ClientsAdapter;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class ClientsListFragment extends Fragment implements SearchView.OnQueryTextListener,
        ClientsAdapter.OnClientDeletedListener,
        ClientsAdapter.OnClientAddListener,
        AdapterView.OnItemClickListener{

    final static public String TAG = "clients_list";
    private List<Customer> list;
    private ClientsAdapter adapter;
    private OnClientSelectedListener clientSelectedListener;
    private int clientSelected;

    private ImageView oldClient;

    //UI elements
    private ListView clientsList;
    private View emptyView;
    //Flag to hide/show the delete button of the list items
    public static boolean hideButtons = true;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_clients_list, container, false);

        clientsList = (ListView) view.findViewById(R.id.clients_list);
        emptyView = view.findViewById(R.id.empty_view);

        list = new ArrayList<Customer>();
        adapter = new ClientsAdapter(getActivity(), list);
        adapter.setOnClientDeletedListener(this);
        clientsList.setAdapter(adapter);
        clientsList.setOnItemClickListener(this);

        //Populates the list view
        populateList();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Changes the ActionBar title when the fragment is attached
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.CLIENTS);

        //Makes sure that MainActivity implements the OnClientSelectedListener interface
        try {
            clientSelectedListener = (OnClientSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClientSelectedListener");

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Notifies the system that this fragment wants to participate on the creation of the menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //If the navigation drawer is closed, clears the previous menu and inflates the new menu
        //otherwise don't do anything
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.clients, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //Initialize the menu items
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        MenuItem searchClient = menu.findItem(R.id.search_client);
        MenuItem deleteButton = menu.findItem(R.id.delete);
        MenuItem editButton = menu.findItem(R.id.edit);
        MenuItem saveButton = menu.findItem(R.id.save);
        MenuItem discardButton = menu.findItem(R.id.discard);
        MenuItem addButton = menu.findItem(R.id.add);

        Fragment clientsAddEdit = fragmentManager.findFragmentByTag(ClientsAddEditFragment.TAG);
        Fragment clientsDetail = fragmentManager.findFragmentByTag(ClientsDetailFragment.TAG);

        //Depending on the currently added fragment hide/show the right menu buttons
        if (clientsAddEdit != null && clientsAddEdit.isAdded()) {

            if (saveButton != null)
                saveButton.setVisible(true);

            if (discardButton != null)
                discardButton.setVisible(true);

            if (addButton != null)
                addButton.setVisible(false);

            if (editButton != null)
                editButton.setVisible(false);

            if (searchClient != null)
                searchClient.setVisible(false);

            if (deleteButton != null)
                deleteButton.setVisible(false);

        } else if (clientsDetail != null && clientsDetail.isAdded()) {

            if (searchClient != null) {
                searchClient.setVisible(true);
                SearchView searchView = (SearchView)searchClient.getActionView();
                searchView.setOnQueryTextListener(this);
                searchClient.setEnabled(!list.isEmpty());
            }
            if (deleteButton != null) {
                deleteButton.setVisible(true);
                deleteButton.setEnabled(!list.isEmpty());
            }
            if (addButton != null)
                addButton.setVisible(true);

            if (editButton != null)
                editButton.setVisible(true);

            if (saveButton != null)
                saveButton.setVisible(false);

            if (discardButton != null)
                discardButton.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Creates instances of the elements that will be needed on the switch statement
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ClientsAddEditFragment clientsAddEdit = (ClientsAddEditFragment) fragmentManager.findFragmentByTag(ClientsAddEditFragment.TAG);

        //Obtains the id of the item selected and executes the right action
        switch (item.getItemId()) {
            case R.id.add:
                //Changes the view for a ClientsAddEditFragment new instance
                fragmentTransaction.replace(R.id.right_container, new ClientsAddEditFragment(), ClientsAddEditFragment.TAG).commit();
                break;
            case R.id.delete:
                //Toggles the flag and redraws the list view
                hideButtons = !hideButtons;
                adapter.notifyDataSetChanged();
                break;
            case R.id.edit:
                //Changes the view for a ClientsAddEditFragment new instance with the id of the client to edit
                fragmentTransaction.replace(
                        R.id.right_container,
                        ClientsAddEditFragment.newInstance(list.get(clientSelected).getId()),
                        ClientsAddEditFragment.TAG).commit();
                break;
            case R.id.save:
                //Saves the client and refreshes the list view
                clientsAddEdit.saveClient();
                populateList();
                break;
            case R.id.discard:
                //Depending on the state of the list view clears the fields or changes the view
                //for a ClientsDetailFragment new instance with the id of the last selected client
                if (list.isEmpty())
                    clientsAddEdit.clearInputs();
                else
                    fragmentTransaction.replace(
                            R.id.right_container,
                            ClientsDetailFragment.newInstance(list.get(clientSelected).getId()),
                            ClientsDetailFragment.TAG).commit();
                break;
        }
        return true;
    }

    private void populateList() {

        //Clears the list and adds all clients on the DB
        list.clear();
        list.addAll(Customer.getAll(getActivity()));

        adapter.notifyDataSetChanged();

        //If the list is empty show the empty view and hide the list
        if (list.isEmpty()) {
            clientsList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            clientsList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Finds the fragment and replaces it with a new instance with the updated information
        ClientsDetailFragment clientsDetail = (ClientsDetailFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ClientsDetailFragment.TAG);

        if (clientsDetail == null) {
            clientsDetail = ClientsDetailFragment.newInstance(list.get(position).getId());
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.right_container, clientsDetail, ClientsDetailFragment.TAG).commit();
        }
        //Calls the interface method and updates the clientSelected flag
        clientSelectedListener.onClientSelected(list.get(position));
        clientSelected = position;

        ImageView currentView = (ImageView)view.findViewById(R.id.client_photo);
        currentView.setImageResource(R.drawable.icon_current_client);
        adapter.notifyDataSetChanged();


        if(oldClient != null){
            oldClient.setImageResource(R.drawable.icon_no_profile);
            adapter.notifyDataSetChanged();
        }
        oldClient = currentView;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        //Filters the client list
        adapter.filterClients(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        //Filters the client list
        adapter.filterClients(s);
        clientsList.setSelectionAfterHeaderView();
        return false;
    }

    @Override
    public void onClientDeleted(int position) {


        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //When a client is deleted if the list is empty, adds a new instance of ClientsAddEditFragment and shows the empty view
        if (list.isEmpty()) {
            fragmentTransaction.replace(R.id.right_container, new ClientsAddEditFragment(), ClientsAddEditFragment.TAG).commit();
            clientsList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            hideButtons = true;

        }
        //If the list is not empty and the deleted client is the same as the one on ClientsDetailFragment select the first client on the list
        else if(clientSelected == position) {

            ClientsDetailFragment clientsDetail = (ClientsDetailFragment) fragmentManager.findFragmentByTag(ClientsDetailFragment.TAG);

            if (clientsDetail == null) {
                clientsDetail = ClientsDetailFragment.newInstance(list.get(0).getId());
                fragmentTransaction.replace(R.id.right_container, clientsDetail, ClientsDetailFragment.TAG).commit();
            }
            clientSelectedListener.onClientSelected(list.get(0));
            clientSelected = 0;
        }
    }

    @Override
    public void onClientAdd(Customer customer) {
        //Here the callback for update the current list!
    }

    public interface OnClientSelectedListener {
        public void onClientSelected(Customer client);
    }

}