package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DatabaseAdapter;

import static util.LogUtil.makeLogTag;

/**
 * Created by juanc.jimenez on 29/08/14.
 */
public class Module extends Table {

    private static final String TAG = makeLogTag(Module.class);
    //Table Name
    public static final String TABLE = "module";

    public static final String ID = "id";
    public static final String NAME = "name";


    private int id;
    private String name;

    public static long insert(Context context, String name) {
        ContentValues cv = new ContentValues();

        cv.put(NAME, name);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Module getModule(Context context, int id){

        Cursor mC = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (mC != null && mC.moveToFirst()) {

            int current_id = mC.getInt(mC.getColumnIndexOrThrow(ID));
            String current_name = mC.getString(mC.getColumnIndexOrThrow(NAME));
            mC.close();

            return new Module(current_id, current_name);
        }
        return null;
    }

    private Module(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
