package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DatabaseAdapter;

/**
 * Created by jcenteno on 09/06/14.
 */
public class RecordActions extends Table {

    public static final String TABLE = "record_actions";

    //fields
    public static final String ID = "id";
    public static final String DATA = "data";
    public static final String NAME_WEBSERVICE = "name_webservice";
    public static final String USER_ID = "user_id";

    public static long insert(Context context, int id,String data, String name_webservice, int user_id) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(DATA, data);
        cv.put(NAME_WEBSERVICE, name_webservice);
        cv.put(USER_ID, user_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Cursor getParamsComplete(Context context){
        Cursor mC;
        mC = DatabaseAdapter.getDB(context).query(TABLE, null,null,null, null, null, null);
        if (mC != null)
            mC.moveToFirst();
        return mC;
    }

    public static Cursor getParamsByTimeStamp(Context context,String id_){
        Cursor mC;
        mC = DatabaseAdapter.getDB(context).query(TABLE, null,ID+"=?",new String []{id_}, null, null, null);
        if (mC != null )
            mC.moveToFirst();
        return mC;
    }

    public static void deleteParam(Context context,String id_){

        DatabaseAdapter.getDB(context).delete(TABLE, ID+"=?", new String[]{id_}) ;

    }
}
