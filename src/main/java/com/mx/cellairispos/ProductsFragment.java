package com.mx.cellairispos;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import database.models.Product;
import database.models.ProductBrand;
import database.models.ProductPic;
import util.ProductsAdapter;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class ProductsFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{

    public static final String TAG = "products";

    private boolean isGrid = false;
    private OnProductSelectedListener productSelectedListener;

    //ListView elements
    private ProductsAdapter adapter;
    private List<Product> list;
    private List<ProductPic> pics;

    //UI elements
    private SearchView searchView;

    //Always we initialize this fragment with the true value
    public static ProductsFragment newInstance(boolean isGrid) {

        //Creates a new instance of the fragment with the value to check is it's on grid or list mode
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_grid", isGrid);
        ProductsFragment products = new ProductsFragment();
        products.setArguments(bundle);
        return products;
    }

    //Saving the instance of the bundle is the perfect way to know the "view" we are trying on
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Reads the arguments to see if it's on grid or list mode
        Bundle extras = getArguments();
        if (extras != null) {
            isGrid = extras.getBoolean("is_grid", false);
        }
        //Provisional: Creates Products in the DB to test the app
        //populateDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_products, container, false);


        //We retrieve all dummy data from product table
        try {
            list = Product.getAll(getActivity());
            pics = ProductPic.getAll(getActivity());
        }catch(Exception e){
            Log.v("In list we have exception", e.toString());
        }

        //The grid element of UI interface
        GridView productsList = (GridView) view.findViewById(R.id.products_grid);

        if (isGrid) {
            //If it's on grid mode set grid values
            productsList.setColumnWidth(96);
            productsList.setNumColumns(GridView.AUTO_FIT);
            productsList.setHorizontalSpacing(16);
        }

        adapter = new ProductsAdapter(getActivity(), isGrid, list, pics);
        productsList.setAdapter(adapter);
        productsList.setOnItemClickListener(this);

        //we retrieve the empty_view
        View emptyView = view.findViewById(R.id.empty_view);

        //Hide/show the empty view depending on what's read from DB
        if (!list.isEmpty())
            emptyView.setVisibility(View.GONE);
        return view;
    }

    public void populateDB() {

        //Creates temporary products to test the app
        List<Product> products = Product.getAll(getActivity());
        if (products.isEmpty()) {
            int brandId = (int) ProductBrand.insert(getActivity(), "Marca");
            for (int i = 0; i < 2; i++) {
                Product.insert(getActivity(), "7509876520" + i, "Product " + i, "Description", 50.00, 45.00, 1.0f, 10, 100, 0, brandId);
            }
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Notifies the system that this fragment wants to participate on the creation of the menu
        //In this case we are intersting on the searchBar
        setHasOptionsMenu(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //If it's on grid mode show the dialog with the product detail,
        //otherwise call the interface to update the product detail in catalog
        if (isGrid) {
            ProductsDetailFragment productsDetail = ProductsDetailFragment.newInstance(list.get(position).getId(), false);
            productsDetail.show(getActivity().getSupportFragmentManager(), ProductsDetailFragment.TAG);
        } else {
            productSelectedListener.onProductSelected(list.get(position));
        }
    }

    public void scanResult(IntentResult scanResult) {
        //Take the result of the bar code scanner and query the products list with it
        if (scanResult != null) {
            searchView.setQuery(scanResult.getContents(), true);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        adapter.filterProducts(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filterProducts(s);
        return false;
    }

    //Interface of Data
    public interface OnProductSelectedListener{
        public void onProductSelected(Product product);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Makes sure that MainActivity implements the OnProductSelectedListener interface
        try {
            productSelectedListener = (OnProductSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnProductSelectedListener.");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         //If the navigation drawer is closed, clears the previous menu and inflates the new menu
        //otherwise don't do anything
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.products, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //Initialize the menu items
        MenuItem searchClient = menu.findItem(R.id.search_client);

        if (searchClient != null) {

            //Setup the search view for the product list
            searchView = (SearchView)searchClient.getActionView();
            searchView.setOnQueryTextListener(this);

            searchView.setIconifiedByDefault(false);
            searchView.setFocusable(false);

            //if the list is not empty we show the searchBar
            searchClient.setEnabled(!list.isEmpty());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Obtains the id of the item selected and executes the right action
        switch (item.getItemId()) {
            case R.id.scan_button:
                //Starts the bar code scanner
                new IntentIntegrator(getActivity()).initiateScan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
