package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import database.DatabaseAdapter;
import util.Utilities;

/**
 * Created by jcenteno on 09/06/14.
 */
public class ProductPic extends Table {

    public static final String TABLE = "product_pic";

    //fields
    public static final String ID = "id";
    public static final String SOURCE = "source";
    public static final String PRODUCT_ID = "product_id";
    public static final String MAIN = "main";

    private int id;
    private String source;
    private int product_id;
    private int main;

    public ProductPic(int id, String source, int product_id, int main){
        this.id = id;
        this.source = source;
        this.product_id = product_id;
        this.main = main;
    }

    public static long insert(Context context, String source, int product_id, int main) {
        ContentValues cv = new ContentValues();
        cv.put(SOURCE, source);
        cv.put(PRODUCT_ID, product_id);
        cv.put(MAIN, main);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Bitmap getProductPic(Context context, int id_prod){

        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, PRODUCT_ID + "=" + id_prod, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String encodedImage = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
            Bitmap decodedImage = Utilities.stringToBitmap(encodedImage);

            cursor.close();

            return decodedImage;
        }
        return null;
    }

    public static Bitmap getThumbnail(Context context,int id_prod){

        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, PRODUCT_ID + "=" + id_prod, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String encodedImage = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
            Bitmap decodedImage = Utilities.stringToBitmap(encodedImage);

            Bitmap scaledImage = Bitmap.createScaledBitmap(decodedImage, 100, 100 * decodedImage.getHeight() / decodedImage.getWidth(), false);
            cursor.close();

            return scaledImage;
        }
        return null;
    }

    public static int delete(Context context, int id) {

        return DatabaseAdapter.getDB(context).delete(TABLE, PRODUCT_ID + "=" + id, null);
    }

    public static String getSource(Context context, int id){
        String source = null;
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()){

            source = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
            cursor.close();
        }
        return source;
    }

    public static ArrayList<ProductPic> getAll(Context context){

        ArrayList<ProductPic> list = new ArrayList<ProductPic>();
        try {
            Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                    String source = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
                    int product_id = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
                    int main = cursor.getInt(cursor.getColumnIndexOrThrow(MAIN));

                    list.add(new ProductPic(id, source, product_id, main));
                }

                cursor.close();
            }
        }catch(Exception e){
            Log.v("EXCEPTION IN CURSOR", e.toString());
        }
        return list;
    }

    public static ProductPic getProductByIdProduct(Context context, int id_product){
        String []args = new String[]{ Integer.toString(id_product) };
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, PRODUCT_ID + "=?", args, null, null, null );

        if(cursor != null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            String source = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
            int product_id = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
            int main = cursor.getInt(cursor.getColumnIndexOrThrow(MAIN));
            return new ProductPic(id, source, product_id, main);
        }
        return null;
    }

    public String getSource(){
        return source;
    }
}
