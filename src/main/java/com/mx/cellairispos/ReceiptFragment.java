package com.mx.cellairispos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import database.DatabaseHelper;
import database.models.Customer;
import database.models.Receipt;
import database.models.Sale;
import database.models.SaleItem;
import database.models.User;
import util.HandleSession;
import util.LogUtil;
import util.ReceiptAdapter;

/**
 * Created by juanc.jimenez on 14/08/14.
 */
public class ReceiptFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    public static final String TAG = "receipt";

    private int transactionId, clientId = -1;

    //UI elements
    private TextView receiptNumber, receiptDate, receiptSubtotal, receiptTaxes, receiptTotal, receiptItemsTotal, activeUser, textAddress;
    private ImageView receiptBarcode;
    private ListView productsList;
    private TextView addClient;
    private Spinner clientsSpinner;

    //Flags
    private boolean isReadyToSave = false, userClicked = false;

    //Constant values for the colors of the bar code
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    //Spinner elements
    private List<String> customerNames;
    private ArrayAdapter<String> spinnerAdapter;

    public static ReceiptFragment newInstance(int id) {
        //Creates a new instance of the fragment with the sale id to generate the receipt
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        ReceiptFragment fragmentReceiptPreview = new ReceiptFragment();
        fragmentReceiptPreview.setArguments(bundle);

        return fragmentReceiptPreview;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Reads the arguments to get the value of the sale id
        Bundle bundle = getArguments();
        if(bundle != null) {
            transactionId = bundle.getInt("id", 0);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //When it's dialog mode, remove the title
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_receipt);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);

        receiptNumber = (TextView) view.findViewById(R.id.receipt_number);
        receiptDate = (TextView) view.findViewById(R.id.receipt_date_time);
        receiptItemsTotal = (TextView) view.findViewById(R.id.receipt_items_total);
        receiptSubtotal = (TextView) view.findViewById(R.id.receipt_subtotal);
        receiptTaxes = (TextView) view.findViewById(R.id.receipt_tax);
        receiptTotal = (TextView) view.findViewById(R.id.receipt_total);
        receiptBarcode = (ImageView) view.findViewById(R.id.receipt_barcode);
        productsList = (ListView) view.findViewById(R.id.products_list);
        addClient = (TextView) view.findViewById(R.id.add_client_button);
        activeUser = (TextView) view.findViewById(R.id.username);
        /*
        textAddress = (TextView)view.findViewById(R.id.address_payment);
        String sucursal = BranchOffice.getBranchOffice.getAddress(getApplication());
        textAddress.setText(sucursal);

        String current_user = HandleSession.getCurrentUser(getActivity());
        activeUser.setText(current_user);
        */

        //We setting the listener to addCLient
        addClient.setOnClickListener(this);
        view.findViewById(R.id.print_receipt_button).setOnClickListener(this);
        view.findViewById(R.id.email_receipt_button).setOnClickListener(this);

        //We retrieve the spinner from xml selected client
        clientsSpinner = (Spinner) view.findViewById(R.id.client_spinner);

        //Initialize the List of names
        customerNames = new ArrayList<String>();
        //Creating the adapter for our spiner of clients with the customerNames as data
        spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, customerNames);
        //Setting adapter
        clientsSpinner.setAdapter(spinnerAdapter);
        //Setting listener interface
        clientsSpinner.setOnItemSelectedListener(this);

        //We populate the spinner adapter with data retrieving of databases;
        populateSpinner();


        //Creating the list of our current "SALE" then we need to attach to list adapter
        //The method createSalesList make a list of elements and also update the layout of the receiver
        List receiptItems = createSalesList();
        ReceiptAdapter receiptAdapter = new ReceiptAdapter(getActivity(), receiptItems);
        productsList.setAdapter(receiptAdapter);
        return view;
    }

    //THe purpose of this method is to populate the arraylist linked to spinner adapter
    public void populateSpinner() {

        //Read all the costumers on DB and get their names to populate the spinner
        List<Customer> customers = Customer.getAll(getActivity());
        //We cleaning the current list with the current objects inside of it
        customerNames.clear();
        //We iterate all clients we add it to customerNames, the list associated to our adapter of spinner
        for (Customer item : customers) {
            customerNames.add(item.getInfo().getFirstName());
        }
        //Add the default option
        customerNames.add(0, getString(R.string.client_spinner_hint));

        //Refresh spinner adapter
        spinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //When the dialog is closed, if everything is ready, proceeds to save ir on the DB
        //otherwise delete the sale and all the items related to it so a new receipt can be
        //created with updated values (in case the sale is modified)
        if (isReadyToSave) {
            saveReceipt();
            ((SalesListFragment)getTargetFragment()).clearList();
        } else {
            Sale.delete(getActivity(), transactionId);
            SaleItem.deleteByReceivingId(getActivity(), transactionId);
        }
    }


    public List<SaleItem> createSalesList() {

        //Get all items related to a current sale, by the id passed from the initialize the fragment
        List<SaleItem> saleItems = SaleItem.getBySaleId(getActivity(), transactionId);
        double subTotal = 0;
        double total;
        double taxes = 0;

        //Iterating the current sale items
        for (SaleItem item : saleItems) {
            //Query the list to calculate the subtotal of the sale
            double price = item.getPriceUnit() * item.getQuantity();
            //Adding to subtotal the price of the current element of list
            subTotal += price;
        }

        //Set the subtotal, tax and total value for the sale in the layout
        DecimalFormat form = new DecimalFormat("0.00");
        total = subTotal + (subTotal * taxes);
        receiptSubtotal.setText("$" + form.format(subTotal));
        receiptTaxes.setText(form.format(taxes) + "%");
        receiptTotal.setText("$" + form.format(total));

        //Get the sale object
        Sale sale = Sale.getSale(getActivity(), transactionId);

        try {
            //Get the date and time of the sale and use it to generate
            //the receipt number and with that number create a bar code
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            Date date = formatter.parse(sale.getTimestamp());
            String receiptId = "#" + String.valueOf(date.getTime() / 1000)
                    + "-" + String.valueOf(sale.getId());

            receiptNumber.setText(receiptId);
            generateBarcode(receiptId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Set the date/time of the sale and the total of items on the sale
        receiptDate.setText(sale.getTimestamp());
        receiptItemsTotal.setText(saleItems.size() + " " + getString(R.string.receipt_items_total));
        /*
        Sets the user name to the receipt
        int userId = sale.getSession().getUser_id();
        User user = User.getUser(getActivity(), userId);
        activeUser.setText(user.getPeople().getFullName());

         OR

        String current_user = HandleSession.getCurrentUser(getActivity());
        activeUser.setText(current_user);
        */

       return saleItems;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.email_receipt_button:
                //Check if the receipt is complete and if so save it, close the dialog
                //and send the receipt attached to an email
                //THe receiptIsComplete is a method for animate and announce if no client was selected
                if (receiptIsComplete()) {
                    isReadyToSave = true;
                    dismiss();
                }
//                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setType("text/plain");
//                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
//                emailIntent.putExtra(Intent.EXTRA_STREAM, image);
//                startActivity(emailIntent);
                break;

            case R.id.print_receipt_button:
                //Check if the receipt is complete and if so save it, close the dialog
                //and print it (Still needs to implement the print functionality)
                if (receiptIsComplete()) {
                    isReadyToSave = true;
                    dismiss();
                }
                break;

            case R.id.add_client_button:
                //Shows the add client dialog
                ClientsAddEditFragment addEditFragment = new ClientsAddEditFragment();
                addEditFragment.setTargetFragment(this, 2);
                addEditFragment.show(getActivity().getSupportFragmentManager(), ClientsAddEditFragment.TAG);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Updates the value of the client selected to keep track of it.
        //userClicked is used to skip onItemSelected when is fired by the system (on the spinner initialization)
        if (userClicked) {
            clientId = position;
        }
        userClicked = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean receiptIsComplete() {

        //Check if a client has been selected on the spinner
        if (clientId < 0) {

            //If no client is selected on the spinner notify the user by blinking the spinner
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    //When animation is finished set the default background to the spinner
                    clientsSpinner.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_button_background_red));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            //Setup the animation and change the background of the spinner to get user's attention
            anim.setDuration(100);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(2);
            clientsSpinner.setBackgroundColor(getResources().getColor(R.color.red));
            clientsSpinner.startAnimation(anim);
            return false;
        }
        else
            return true;
    }

    public void saveReceipt() {

        //Create a list of bitmaps where the receipt items will be added
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();

        //Hide the add client button to exclude it from the image receipt
        addClient.setVisibility(View.GONE);
        //For convenience the receipt_layout is divided on 3 sections, header, list and footer
        View receiptHeader = getView().findViewById(R.id.receipt_header_layout);
        View receiptFooter = getView().findViewById(R.id.receipt_footer_layout);

        //Convert the views to bitmaps and add them to the bitmap list
        bitmapList.add(viewToBitmap(receiptHeader));
        bitmapList.add(listToBitmap(productsList));
        bitmapList.add(viewToBitmap(receiptFooter));

        //Create a linear layout to place all the views of the receipt
        LinearLayout receiptLayout = new LinearLayout(getActivity());
        receiptLayout.setOrientation(LinearLayout.VERTICAL);
        receiptLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.receipt));
        receiptLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        receiptLayout.setPadding(32, 48, 32, 48);

        //Add all the bitmaps  from the list as image view to the linear layout
        for (Bitmap image : bitmapList) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setAdjustViewBounds(true);
            imageView.setImageBitmap(image);

            receiptLayout.addView(imageView);
        }

        //Finally renders the receipt into one last bitmap and save it to the DB
        receiptLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        receiptLayout.layout(0, 0, receiptLayout.getMeasuredWidth(), receiptLayout.getMeasuredHeight());
        receiptLayout.setDrawingCacheEnabled(true);
        receiptLayout.buildDrawingCache();
        Bitmap image = Bitmap.createBitmap(receiptLayout.getDrawingCache());

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);

            byte[] bytes = outputStream.toByteArray();
            String encodedImaged = Base64.encodeToString(bytes, Base64.DEFAULT);
            Receipt.insert(getActivity(), transactionId, encodedImaged);

            Sale.update(getActivity(), transactionId, DatabaseHelper.NOT_UPDATE, DatabaseHelper.NOT_UPDATE, clientId, transactionId);

            outputStream.flush();
            outputStream.close();

            //Show the created image on a dialog as a preview
            showReceipt(transactionId);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Bitmap viewToBitmap(View view) {

        //Converts a view and all it's children into a bitmap
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap image = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return image;
    }

    public Bitmap listToBitmap(ListView list) {

        //Converts a list view and all it's items into a bitmap
        int listHeight = 0;
        int itemHeight = 0;
        ListAdapter listAdapter = list.getAdapter();
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            //Get one by one the child views, convert them into a bitmap and add them to a list of bitmaps
            View childView = listAdapter.getView(i, null, list);

            childView.measure(View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());

            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bitmapList.add(childView.getDrawingCache());
            listHeight += childView.getHeight();
        }

        //Creates on big bitmap with all the images previously created
        Bitmap finalBitmap = Bitmap.createBitmap(list.getMeasuredWidth(), listHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalBitmap);

        Paint paint = new Paint();

        for (Bitmap bitmap : bitmapList) {

            canvas.drawBitmap(bitmap, 0, itemHeight, paint);
            itemHeight += bitmap.getHeight();
        }

        return finalBitmap;
    }

    public void generateBarcode(String data) {

        //Obtain a bar code from a string and place it on the proper place in the receipt
        try {
            Bitmap bitmap = encodeAsBitmap(data, BarcodeFormat.CODE_128, 400, 50);
            receiptBarcode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {

        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);

        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;

        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }


    public void showReceipt(int id) {

        //Shows a dialog with the preview of a saved receipt
        Bitmap receipt = Receipt.getImage(getActivity(), id);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog);

        ImageView previewItem = new ImageView(getActivity());
        previewItem.setAdjustViewBounds(true);
        previewItem.setImageBitmap(receipt);

        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.setFillViewport(true);
        scrollView.addView(previewItem);

        dialogBuilder.setTitle(getString(R.string.receipt_saved));
        dialogBuilder.setView(scrollView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }
}
