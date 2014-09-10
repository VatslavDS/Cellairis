package com.mx.cellairispos;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import database.models.Customer;
import database.models.People;

/**
 * Created by juanc.jimenez on 14/08/14.
 */
public class ClientsAddEditFragment extends DialogFragment implements View.OnClickListener{

    final static public String TAG = "clients_add_edit";

    //UI elements
    private EditText clientName, clientTIN, clientPhone, clientEmail, clientAddress1, clientAddress2;
    private EditText clientCity, clientState, clientCountry, clientZipCode, clientComments;
    private Customer clientToEdit;

    //Helps to know whether we are in edit mode or dialog mode
    private boolean isEditing = false;
    private boolean isDialog = false;

    public static ClientsAddEditFragment newInstance(int clientId) {

        //Creates a new instance of the fragment with the id of the client to edit
        Bundle bundle = new Bundle();
        bundle.putInt("id", clientId);
        ClientsAddEditFragment clientsAddEdit = new ClientsAddEditFragment();
        clientsAddEdit.setArguments(bundle);
        return clientsAddEdit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //When it's dialog mode, remove the title and change the flag to true
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isDialog = true;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Reads the arguments to see if there's a client to edit or it's adding a new one
        Bundle bundle = getArguments();
        if(bundle != null) {
            //If there are arguments, read the client id and change the flag to true
            int id = bundle.getInt("id", 0);
            clientToEdit = Customer.getCustomer(getActivity(), id);
            isEditing = true;
        }
        //Refresh the ActionBar menu (see ClientsListFragment:onPrepareOptionsMenu)
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_clients_add_edit, container, false);

        clientName = (EditText) view.findViewById(R.id.client_name);
        clientTIN = (EditText) view.findViewById(R.id.client_rfc);
        clientPhone = (EditText) view.findViewById(R.id.client_phone);
        clientEmail = (EditText) view.findViewById(R.id.client_email);
        clientAddress1 = (EditText) view.findViewById(R.id.client_address_1);
        clientAddress2 = (EditText) view.findViewById(R.id.client_address_2);
        clientCity = (EditText) view.findViewById(R.id.client_city);
        clientState = (EditText) view.findViewById(R.id.client_state);
        clientCountry = (EditText) view.findViewById(R.id.client_country);
        clientZipCode = (EditText) view.findViewById(R.id.client_zip_code);
        clientComments = (EditText) view.findViewById(R.id.client_comments);

        //If it's on edit mode, prepare the UI elements
        if (isEditing)
            setUpForEdit(clientToEdit);

        //If it's on dialog mode initialize the ok/cancel buttons, otherwise hide them
        if (isDialog) {
            view.findViewById(R.id.save_button).setOnClickListener(this);
            view.findViewById(R.id.cancel_button).setOnClickListener(this);
        } else {
            view.findViewById(R.id.buttons_layout).setVisibility(View.GONE);
        }

        return view;
    }

    public void setUpForEdit(Customer customer) {

        //Adds the client data to the UI elements so it can be edited
        clientName.setText(customer.getInfo().getFirstName());
        clientTIN.setText(customer.getInfo().getRfc());
        clientPhone.setText(customer.getInfo().getPhone());
        clientEmail.setText(customer.getInfo().getEmail());
        clientAddress1.setText(customer.getInfo().getAddress1());
        clientAddress2.setText(customer.getInfo().getAddress2());
        clientCity.setText(customer.getInfo().getCity());
        clientState.setText(customer.getInfo().getState());
        clientCountry.setText(customer.getInfo().getCountry());
        clientZipCode.setText(customer.getInfo().getZipCode());
        clientComments.setText(customer.getInfo().getComments());
    }

    public void saveClient() {

        if (hasValidInputs()) {

            //If the inputs are valid reads the fields to save the data
            String name, tin, phone, email, address1, address2, city, state, country, zipCode, comments;

            name = clientName.getText().toString();
            tin = clientTIN.getText().toString();
            phone = clientPhone.getText().toString();
            email = clientEmail.getText().toString();
            address1 = clientAddress1.getText().toString();
            address2 = clientAddress2.getText().toString();
            city = clientCity.getText().toString();
            state = clientState.getText().toString();
            country = clientCountry.getText().toString();
            zipCode = clientZipCode.getText().toString();
            comments = clientComments.getText().toString();

            //If on edit mode, updates the client with the new data, otherwise creates a new client (Customer)
            if (isEditing)
                People.update(getActivity(), clientToEdit.getInfo().getId(), name, "null", email, tin, phone, address1, address2, city, state, country, comments, zipCode, "null");
            else {
                int peopleId = (int) People.insert(getActivity(), name, "null", email, tin, phone, address1, address2, city, state, country, comments, zipCode, "null");
                Customer.insert(getActivity(), 0, 0, peopleId);
            }

            //Clears the screen, changes the edit flag and notifies the user that the client has been saved
            clearInputs();
            isEditing = false;
            Toast.makeText(getActivity(), getString(R.string.client_saved), Toast.LENGTH_SHORT).show();
        }
    }

    public void clearInputs() {

        //Clears the fields on the UI
        clientName.setText("");
        clientTIN.setText("");
        clientPhone.setText("");
        clientEmail.setText("");
        clientAddress1.setText("");
        clientAddress2.setText("");
        clientCity.setText("");
        clientState.setText("");
        clientCountry.setText("");
        clientZipCode.setText("");
        clientComments.setText("");
    }

    private boolean hasValidInputs() {

        String name, tin, phone, email, address1, city, state, country, zipCode;
        boolean isValid = true;

        //Reads the inputs of the UI
        name = clientName.getText().toString();
        tin = clientTIN.getText().toString();
        phone = clientPhone.getText().toString();
        email = clientEmail.getText().toString();
        address1 = clientAddress1.getText().toString();
        city = clientCity.getText().toString();
        state = clientState.getText().toString();
        country = clientCountry.getText().toString();
        zipCode = clientZipCode.getText().toString();

        //If any field is empty throws and error to the user
        if (name.isEmpty()) {
            clientName.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (tin.isEmpty()) {
            clientTIN.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (phone.isEmpty()) {
            clientPhone.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (email.isEmpty()) {
            clientEmail.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        //If the email field doesn't have the format something@something throws and error to the user
        if (email.indexOf("@") == 0 || email.indexOf("@") == email.length() - 1 || !email.contains("@")) {
            clientEmail.setError(getString(R.string.error_invalid_email_address));
            isValid = false;
        }
        if (address1.isEmpty()) {
            clientAddress1.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (city.isEmpty()) {
            clientCity.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (state.isEmpty()) {
            clientState.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (country.isEmpty()) {
            clientCountry.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (zipCode.isEmpty()) {
            clientZipCode.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        //If zip code is shorter than 5 characters throws an error
        if (zipCode.length() != 5) {
            clientZipCode.setError(getString(R.string.error_invalid_zip_code));
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onClick(View v) {

        //Actions for the buttons in dialog mode
        switch (v.getId()) {
            case R.id.save_button:
                //Saves the client, refreshes the client spinner (ReceiptFragment) and closes the dialog
                saveClient();
                ((ReceiptFragment)getTargetFragment()).populateSpinner();
                dismiss();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
        }

    }
}
