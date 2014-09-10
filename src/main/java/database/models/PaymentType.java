package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DatabaseAdapter;

/**
 * Created by juanc.jimenez on 30/06/14.
 */
public class PaymentType extends Table {

    public static final String TABLE = "payment_type";

    public static final String ID = "id";
    public static final String NAME = "name";

    public static long insert(Context context, String name){

        ContentValues cv = new ContentValues();
        cv.put(NAME, name);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static String getName(Context context, int id) {

        String name = null;
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));

            cursor.close();
        }
        return name;
    }
}
