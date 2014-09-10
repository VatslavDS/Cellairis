package com.mx.cellairispos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import database.models.Receipt;
import database.models.Sale;
import util.LogUtil;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class PaymentDetailFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "payments_detail";

    //UI elements
    private ImageView receipt;
    private Bitmap image;
    private Button print, email;

    private static List<Sale> all;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_payment_detail, container, false);

        receipt = (ImageView) view.findViewById(R.id.receipt);
        print = (Button) view.findViewById(R.id.print_receipt_button);
        email = (Button) view.findViewById(R.id.email_receipt_button);

        email.setOnClickListener(this);
        print.setOnClickListener(this);

        //If nothing found on DB, disable buttons to prevent the user to use them,
        //otherwise show the first item on the list
        all = Sale.getAll(getActivity());
        if (all.isEmpty()) {
            print.setEnabled(false);
            email.setEnabled(false);
        } else
            updateReceipt(all.get(0).getId());

        return view;
    }

    public void updateReceipt(int receiptId) {

        //Reads the image from DB and shows it on screen
        image = Receipt.getImage(getActivity(), receiptId);

        print.setEnabled(image != null);
        email.setEnabled(image != null);

        receipt.setImageBitmap(image);
    }

    @Override
    public void onClick(View v) {

        //Handles the buttons click (No functions added yet)
        switch (v.getId()) {
            case R.id.print_receipt_button:
                break;
            case R.id.email_receipt_button:
                Bitmap current_receipt = Receipt.getImage(getActivity(), all.get(0).getId());
                Bitmap new_bitmap = scaleDownBitmap(current_receipt, 10, getActivity());

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("application/image");
                emailIntent.putExtra(Intent.EXTRA_STREAM, new_bitmap);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vatslavds@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "RECIBO");
                startActivity(emailIntent);
                break;
        }
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }
}
