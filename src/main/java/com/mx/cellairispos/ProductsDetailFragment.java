package com.mx.cellairispos;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import database.models.Product;
import database.models.ProductPic;
import util.LogUtil;

/**
 * Created by juanc.jimenez on 12/08/14.
 */

//The DialogFragment for detail product
public class ProductsDetailFragment extends DialogFragment implements View.OnClickListener{

    public static final String TAG = "products_detail";

    //Helps to know whether we are in dialog mode or not
    private boolean isDialog = false;

    //Interface callback
    private OnProductAddedListener productAddedListener;

    //UI elements
    private TextView productName, productPrice, productDescription, productExit, productAdd;
    private ImageView productImage;

    //Object holding the data to populate the view
    private Product product;

    private static boolean isGreatCatalog = false;

    public static ProductsDetailFragment newInstance(int productId, boolean isCatalog) {

        //Creates a new instance of the fragment with the id of the product to edit
        Bundle bundle = new Bundle();
        bundle.putInt("product_id", productId);
        ProductsDetailFragment detail = new ProductsDetailFragment();
        detail.setArguments(bundle);

        isGreatCatalog = isCatalog;
        return detail;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        //The UI elements for process
        productName = (TextView) view.findViewById(R.id.product_name);
        productPrice = (TextView) view.findViewById(R.id.product_price);
        productDescription = (TextView) view.findViewById(R.id.product_description);
        productImage = (ImageView) view.findViewById(R.id.product_image);
        productExit = (TextView)view.findViewById(R.id.exit);
        productAdd = (TextView)view.findViewById(R.id.buy_button);

        if(isGreatCatalog){
            productExit.setVisibility(View.GONE);
            productAdd.setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.buy_button).setOnClickListener(this);
            view.findViewById(R.id.exit).setOnClickListener(this);
        }
        //Reads the arguments to see if there's a product to edit or it's adding a new one
        Bundle extras = getArguments();
        if (extras != null) {
            //If there are arguments, read the product id and read the data from DB
            int productId = extras.getInt("product_id", 1);
            product = Product.getProduct(getActivity(), productId);
            updateDetail(product);
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        //Buy button handler
        //If on dialog mode add the product to the shop list, else show the SalesFragment
        if (isDialog) {
            if(v.getId() == R.id.exit){
                dismiss();
            }else {
                //The method dismiss() will close the dialog....
                dismiss();
                //In this listener or callback we add a new product to the other fragment
                productAddedListener.onProductAdded(product);
            }
        } else {
            //this will never occur
            NavigationDrawerFragment drawer = (NavigationDrawerFragment)
                    getActivity().getFragmentManager().findFragmentById(R.id.navigation_drawer);
            if (drawer != null) {
                drawer.selectItem(NavigationDrawerFragment.SALES);
            }
        }
    }

    public void updateDetail(Product product) {
        //Updates the UI with the new values
        ProductPic current = ProductPic.getProductByIdProduct(getActivity(), product.getId());
        productName.setText(product.getName());
        DecimalFormat form = new DecimalFormat("0.00");
        productPrice.setText("$" + form.format(product.getPrice()));
        productDescription.setText(product.getDescription());
        try{
            productImage.setImageBitmap(readImageFromExternal(current.getSource()));
        }catch(Exception e){
            productImage.setImageResource(R.drawable.icon_product_empty);
        }
    }

    public interface OnProductAddedListener{
        public void onProductAdded(Product product);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Makes sure that MainActivity implements the OnProductAddedListener interface
        try {
            productAddedListener = (OnProductAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnProductAddedListener.");
        }
    }

    public Bitmap readImageFromExternal(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + path, options);
        return bitmap;
    }

}