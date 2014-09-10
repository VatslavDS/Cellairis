package com.mx.cellairispos;


import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import database.models.Customer;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class  ClientsDetailFragment extends Fragment {

    final static public String TAG = "clients_detail";

    //Object holding the data to populate the view
    private Customer client;
    //UI elements
    private TextView clientName, clientPhone, clientEmail, clientTIN, clientAddress;

    //Geocoder object
    Geocoder geoCoder;

    public static ClientsDetailFragment newInstance(int clientId) {

        //Creates a new instance of the fragment with the id of the client to show
        Bundle bundle = new Bundle();
        bundle.putInt("id", clientId);
        ClientsDetailFragment fragmentClientsDetail = new ClientsDetailFragment();
        fragmentClientsDetail.setArguments(bundle);
        return fragmentClientsDetail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Reads the arguments to see if there's a client to edit or it's adding a new one
        Bundle bundle = getArguments();
        if(bundle != null) {
            //If there are arguments, read the client id and read the data from DB
            int id = bundle.getInt("id", 0);
            client = Customer.getCustomer(getActivity(), id);
        }
        //Refresh the ActionBar menu (see ClientsListFragment:onPrepareOptionsMenu)
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_clients_detail, container, false);


        clientName = (TextView) view.findViewById(R.id.client_name);
        clientPhone = (TextView) view.findViewById(R.id.client_phone);
        clientEmail = (TextView) view.findViewById(R.id.client_email);
        clientAddress = (TextView) view.findViewById(R.id.client_address);
        clientTIN = (TextView) view.findViewById(R.id.client_rfc);

        updateDetail(client);

        return view;
    }

    public void updateDetail(Customer client) {

        if (client != null) {
            //If the object client is valid, updates the UI with the new values
            clientName.setText(client.getInfo().getFirstName());
            clientPhone.setText(client.getInfo().getPhone());
            clientEmail.setText(client.getInfo().getEmail());
            clientTIN.setText(client.getInfo().getRfc());

            String fullAddress = client.getInfo().getAddress1()
                    + ", " + client.getInfo().getCity()
                    + ", " + client.getInfo().getState()
                    + ", " + client.getInfo().getCountry() + "\n" + client.getInfo().getZipCode();



            //We need to retrieve the address of the client, we are going to use the first address
            String client_address = client.getInfo().getAddress1();
            String client_country = client.getInfo().getCity();
            clientAddress.setText(fullAddress);
            if(client_address != null || client_address != "" || client_country != null || client_country != ""){
                new DownloadWebPageTask().execute(new String[]{client_address, client_country});
            }
        }
    }

    public void setUpMap(LatLng latlng, int status){
        SupportMapFragment mapFragment = null;
        if(status == ConnectionResult.SUCCESS) {
               mapFragment = SupportMapFragment.newInstance(
                    new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(latlng, 15)));
        }else{
            Toast.makeText(getActivity(), "Google Play Services is unavailable", Toast.LENGTH_SHORT).show();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), ConnectionResult.SUCCESS);
            dialog.show();
        }

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapFragment).commit();
    }

    public  JSONObject getLocationFormGoogle(String placesName) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" +placesName);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jsonObject;
    }

    public  LatLng getLatLng(JSONObject jsonObject) {

        Double lon = new Double(0);
        Double lat = new Double(0);

        try {

            lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new LatLng(lat,lon);

    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... place) {
            if(place.length > 1) {
                String cu_add = place[0].replaceAll("\\s+", "");
                String cu_city = place[1].replaceAll("\\s+", "+");
                LatLng latlng = null;
                latlng = getLatLng(getLocationFormGoogle(cu_add + cu_city));
                SupportMapFragment mapFragment = null;
                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

                //Current location
                setUpMap(latlng, status);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
