package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import database.DatabaseAdapter;
import util.LogUtil;
/**
 * Created by jcenteno on 09/06/14.
 */
public class ProductBrand extends Table {

    public static final String TABLE = "product_brand";

    //fields
    public static final String ID = "id";
    public static final String NAME = "name";

    public static long insert(Context context, String name) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {

        return DatabaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static String getName(Context context, int id) {

        String name = null;
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
            cursor.close();
        }
        return name;
    }

    public static int getId(Context context, String name) {

        int id = 0;
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, NAME + "=?", new String[]{name}, null, null, null);
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            cursor.close();
        }
        return id;
    }

    public static ArrayList<String> getAll(Context context) {

        ArrayList<String> brands = new ArrayList<String>();
        Cursor cursor = DatabaseAdapter.getDB(context).rawQuery("SELECT name FROM product_brand ORDER BY name", null);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String item = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
                brands.add(item);
            }
        }
        return brands;
    }

}
