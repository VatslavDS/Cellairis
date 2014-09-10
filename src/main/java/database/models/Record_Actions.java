package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DatabaseAdapter;

import static util.LogUtil.makeLogTag;

/**
 * Created by juanc.jimenez on 29/08/14.
 */
public class Record_Actions extends Table {
    private static final String TAG = makeLogTag(Record_Actions.class);
    //Table Name
    public static final String TABLE = "record_actions";
    //Datos de Persona
    public static final String ID = "id";
    public static final String DATA = "data";
    public static final String NAME_WEBSERVICE = "name_webservice";
    public static final String USER_ID = "user_id";

    private int id;
    private String data;
    private String name_webservice;
    private int user_id;

    public static long insert(Context context, String data, String name_webservice, int user_id) {
        ContentValues cv = new ContentValues();

        cv.put(DATA, data);
        cv.put(NAME_WEBSERVICE, name_webservice);
        cv.put(USER_ID, user_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Record_Actions getRecordActions(Context context, int id){

        Cursor mC = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (mC != null && mC.moveToFirst()) {

            int current_id = mC.getInt(mC.getColumnIndexOrThrow(ID));
            String current_data = mC.getString(mC.getColumnIndexOrThrow(DATA));
            String current_name_webservice = mC.getString(mC.getColumnIndexOrThrow(NAME_WEBSERVICE));
            int current_user_id = mC.getInt(mC.getColumnIndexOrThrow(USER_ID));
            mC.close();

            return new Record_Actions(current_id, current_data, current_name_webservice, current_user_id);
        }
        return null;
    }

    public static Record_Actions getRecodActionsByUserId(Context context, int user_id){

        Cursor mC = DatabaseAdapter.getDB(context).query(TABLE, null, USER_ID + "=" + user_id, null, null, null, null);
        if(mC != null && mC.moveToFirst()){

            int current_id = mC.getInt(mC.getColumnIndexOrThrow(ID));
            String current_data = mC.getString(mC.getColumnIndexOrThrow(DATA));
            String current_name_webservice = mC.getString(mC.getColumnIndexOrThrow(NAME_WEBSERVICE));
            int current_user_id = mC.getInt(mC.getColumnIndexOrThrow(USER_ID));
            mC.close();

            return new Record_Actions(current_id, current_data, current_name_webservice, current_user_id);
        }
        return null;
    }

    private Record_Actions(int id, String data, String name_webservice, int user_id) {
        this.id = id;
        this.data = data;
        this.name_webservice = name_webservice;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
