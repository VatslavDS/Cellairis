package database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jcenteno on 13/05/14.
 */
public class DatabaseAdapter {

    private static DatabaseHelper mDbHelper = null;
    private static SQLiteDatabase mDb = null;

    public static SQLiteDatabase getDB(Context context) {
        if (mDb == null)
            openDB(context);
        return mDb;
    }

    public static boolean openDB(Context context) {
        if (mDbHelper != null)
            mDbHelper.close();

        mDbHelper = new DatabaseHelper(context);

        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}