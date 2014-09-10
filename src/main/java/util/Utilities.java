package util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by juanc.jimenez on 22/05/14.
 */
public class Utilities {

    public static boolean isHandset(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void hideKeyboard(Context context, View editField) {

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
    }

    public static Bitmap stringToBitmap(String encodedString) {

        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
