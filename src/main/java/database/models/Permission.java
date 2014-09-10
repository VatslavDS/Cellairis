package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.DatabaseAdapter;

import static util.LogUtil.makeLogTag;

/**
 * Created by juanc.jimenez on 29/08/14.
 */
public class Permission extends Table {


    private static final String TAG = makeLogTag(Permission.class);
    //Table Name
    public static final String TABLE = "permission";
    public static final String ID = "id";
    public static final String PROFILE_ID = "profile_id";
    public static final String MODULE_ID = "module_id";


    private int id;
    private int profile_id;
    private int module_id;
    public static long insert(Context context, int profile_id, int module_id) {
        ContentValues cv = new ContentValues();

        cv.put(PROFILE_ID, profile_id);
        cv.put(MODULE_ID, module_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Permission getPermission(Context context, int id){

        Cursor mC = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (mC != null && mC.moveToFirst()) {

            int current_id = mC.getInt(mC.getColumnIndexOrThrow(ID));
            int current_profile_id = mC.getInt(mC.getColumnIndexOrThrow(PROFILE_ID));
            int current_module_id = mC.getInt(mC.getColumnIndexOrThrow(MODULE_ID));


            mC.close();

            return new Permission(current_id, current_profile_id, current_module_id);
        }
        return null;
    }

    public Permission(int id, int profile_id, int module_id) {

        this.id = id;
        this.profile_id = profile_id;
        this.module_id = module_id;
    }

    public int getId() {
        return id;
    }

    public int getProfileId() {
        return profile_id;
    }

    public int getModuleId() {
        return module_id;
    }

}
