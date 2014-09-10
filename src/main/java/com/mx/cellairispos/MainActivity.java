package com.mx.cellairispos;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mx.cellairispos.ProductsFragment.OnProductSelectedListener;
import com.mx.cellairispos.ProductsDetailFragment.OnProductAddedListener;
import com.mx.cellairispos.PaymentListFragment.OnPaymentSelectedListener;
import com.mx.cellairispos.ClientsListFragment.OnClientSelectedListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

import database.models.Customer;
import database.models.Product;
import database.models.ProductPic;
import util.HandleSession;

public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        //The listener for add a new product to SaleList
        OnProductAddedListener,
        OnProductSelectedListener,
        OnPaymentSelectedListener,
        OnClientSelectedListener {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    //Session variable
    private Long session;

    //Token variable
    private String token;

    private CharSequence mTitle;
    public boolean isDrawerOpen;

    //ImageView of Profile
    private ImageView mProfile;

    //The tag for the fragment logout
    private String current_tag_fragment;

    //Keeps record of the clicks made on the back button
    private boolean isFirstBackClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        session = HandleSession.getCurrentSession(this);
        token = HandleSession.getToken(this);
        if(session == 0){
            startActivity(new Intent(this, LoginActivity.class));
        }

        //Start service from fetch data form server

        if(!token.isEmpty()) {
            startService(new Intent(this, util.WebServiceFetchingDataService.class));
        }
        */
        startService(new Intent(this, util.WebServiceFetchingDataService.class));

        //Hide the title always
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.cellairis_actionbar);
        actionBar.setDisplayShowTitleEnabled(false);

        //Inflates the view and initializes the UI elements
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        //According to the selected item on the navigation drawer
        //creates a transaction and changes the fragment to the selected one
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = new Fragment();
        switch (position) {
            case NavigationDrawerFragment.SALES:
                fragment = new SalesFragment();
                current_tag_fragment = fragment.getTag();
                break;
            case NavigationDrawerFragment.CATALOG:
                fragment = new CatalogFragment();
                current_tag_fragment = fragment.getTag();
                break;
            case NavigationDrawerFragment.PAYMENTS_HISTORY:
                fragment = new PaymentHistoryFragment();
                current_tag_fragment = fragment.getTag();
                break;
            case NavigationDrawerFragment.CLIENTS:
                fragment = new ClientsFragment();
                current_tag_fragment = fragment.getTag();
                break;
            case NavigationDrawerFragment.SETTINGS:
                fragment = new SettingsFragment();
                current_tag_fragment = fragment.getTag();
                break;
            case NavigationDrawerFragment.LOG_OUT:
                fragment = new LogOutFragment();
                break;
        }
        /*
        if(session != 0){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(R.id.container, fragment, fragment.getTag()).commit();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }
        */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, fragment, fragment.getTag()).commit();

        //Resets the flag
        isFirstBackClick = true;
    }

    public void onSectionAttached(int number) {
        //Changes the ActionBar title when the fragment is attached
        mTitle = getResources().getStringArray(R.array.drawer_titles)[number];
    }

    public void restoreActionBar() {
        //Redraws the action bar

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Handling customize actionbar
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.title_actionbar, null);
        TextView tv = (TextView)v.findViewById(R.id.title);

        tv.setText(mTitle);
        actionBar.setCustomView(v);
        actionBar.setDisplayShowCustomEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Updates the isDrawerOpen flag and redraws the action bar
        isDrawerOpen = mNavigationDrawerFragment.isDrawerOpen();
        if (!isDrawerOpen) {
            menu.clear();
            restoreActionBar();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    //For the code result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NavigationDrawerFragment.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            //It could be decodeFile or decodeResource or Decode ByteArray
            Bitmap yourSelectedImage = BitmapFactory.decodeFile(picturePath);

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_navigation_drawer, null);
            ImageView profile = (ImageView)view.findViewById(R.id.profile_picture);

            profile.setImageBitmap(yourSelectedImage);


        }
        //Catches the result of the bar code activity and send the result to the corresponding fragment
        FragmentManager manager = getSupportFragmentManager();
        ProductsFragment products = (ProductsFragment) manager.findFragmentByTag(ProductsFragment.TAG);
        if (products != null && products.isAdded()) {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            products.scanResult(scanningResult);
        }


    }

    //Interfaces with the fragments.
    //Finds the necessary fragment and sends the result to it.
    @Override
    public void onProductAdded(Product product) {
        FragmentManager manager = getSupportFragmentManager();

        SalesListFragment salesList = (SalesListFragment) manager.findFragmentByTag(SalesListFragment.TAG);
        if (salesList != null && salesList.isAdded()) {
            salesList.addProductToList(product);
        }
    }

    @Override
    public void onProductSelected(Product product) {
        FragmentManager manager = getSupportFragmentManager();

        ProductsDetailFragment detail = (ProductsDetailFragment) manager.findFragmentByTag(ProductsDetailFragment.TAG);
        if (detail != null && detail.isAdded()) {
            detail.updateDetail(product);
        }
    }

    @Override
    public void onPaymentSelected(int id) {
        FragmentManager manager = getSupportFragmentManager();

        PaymentDetailFragment detail = (PaymentDetailFragment) manager.findFragmentByTag(PaymentDetailFragment.TAG);
        if (detail != null && detail.isAdded()) {
            detail.updateReceipt(id);
        }
    }

    @Override
    public void onClientSelected(Customer client) {
        FragmentManager manager = getSupportFragmentManager();

        ClientsDetailFragment detail = (ClientsDetailFragment) manager.findFragmentByTag(ClientsDetailFragment.TAG);
        if (detail != null && detail.isAdded()) {
            detail.updateDetail(client);
        }
    }

    @Override
    public void onBackPressed() {

        //Handles back button clicks: One click shows a toast. Two clicks closes the app
        if (isFirstBackClick) {
            Toast.makeText(this, R.string.exit_confirmation, Toast.LENGTH_LONG).show();
            isFirstBackClick = false;
        } else {
            moveTaskToBack(true);
            super.onBackPressed();
        }
    }
}