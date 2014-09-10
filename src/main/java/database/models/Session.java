package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.DatabaseAdapter;

import static util.LogUtil.makeLogTag;

/**
 * Created by dmolinero on 27/06/14.
 */
public class Session extends Table {

    private static final String TAG = makeLogTag(Session.class);
    //Table Name
    public static final String TABLE = "session";
    //Datos de Persona
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String TIMESTAMP = "timestamp";
    public static final String TOKEN = "token";
    public static final String DEVICE = "device";
    public static final String USER_ID = "user_id";

    private int id;
    private int status;
    private String timestamp;
    private String token;
    private String device;
    private int user_id;

    public static long insert(Context context, int status ,String token, String device,int user_id) {
        ContentValues cv = new ContentValues();

        cv.put(STATUS, status);
        cv.put(TOKEN, token);
        cv.put(DEVICE, device);
        cv.put(USER_ID, user_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void closeSession(Context context) {
        Cursor active;
        active = DatabaseAdapter.getDB(context).query(TABLE, null, STATUS + "=?", new String[]{"1"}, null, null, null);

            if (active != null && active.getCount() >0 ) {
                active.moveToFirst();
                String id_user = active.getString(active.getColumnIndexOrThrow(USER_ID));
                ContentValues cv = new ContentValues();
                cv.put(STATUS, 0);
                DatabaseAdapter.getDB(context).execSQL("UPDATE "+TABLE+" SET "
                        +"  "+STATUS+"="+0+","
                        +"  "+TIMESTAMP+"="+"(datetime('now','localtime')) "
                        +"WHERE "+USER_ID+"="+id_user);
            }
        }

    public static void updateToken(Context context, int status,String timestamp,String token, String device,int user_id){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues cv = new ContentValues();
            cv.put(TOKEN, token);
            cv.put(STATUS, status);
            cv.put(DEVICE, device);
            cv.put(TIMESTAMP, dateFormat.format(date));

        DatabaseAdapter.getDB(context).execSQL("UPDATE "+TABLE+" SET "+TOKEN+"='"+token+"',"
                                                              +"  "+STATUS+"="+status+","
                                                              +"  "+DEVICE+"='"+device+"',"
                                                              +"  "+TIMESTAMP+"="+"(datetime('now','localtime')) "
                                                              +"WHERE "+USER_ID+"="+user_id);
    }

    public static Cursor getAllSession(Context _context){
        Cursor mC = DatabaseAdapter.getDB(_context).query(TABLE, null, null, null, null, null, null);
        if (mC != null && mC.getCount()>0)
            mC.moveToFirst();
        return mC;
    }

    public static Session getSessionActive(Context _context){
        Cursor mC = DatabaseAdapter.getDB(_context).query(TABLE, null, STATUS + "=?", new String[]{"1"}, null, null, null);
        if (mC != null && mC.getCount()>0) {
            mC.moveToFirst();

            int id = mC.getInt(mC.getColumnIndexOrThrow(ID));
            int status = mC.getInt(mC.getColumnIndexOrThrow(STATUS));
            String timestamp = mC.getString(mC.getColumnIndexOrThrow(TIMESTAMP));
            String token = mC.getString(mC.getColumnIndexOrThrow(TOKEN));
            String device = mC.getString(mC.getColumnIndexOrThrow(DEVICE));
            int user_id = mC.getInt(mC.getColumnIndexOrThrow(USER_ID));

            mC.close();

            return new Session(id, status, timestamp, token, device, user_id);
        }
        return null;
    }

    public static Cursor getSessionActive2(Context _context){
        Cursor mC = DatabaseAdapter.getDB(_context).query(TABLE, null, STATUS + "=?", new String[]{"1"}, null, null, null);
        if (mC != null && mC.getCount()>0) {
            mC.moveToFirst();
        }
        return mC;
    }

    public static Session getSession(Context context, int id){

        Cursor mC = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (mC != null && mC.moveToFirst()) {

            int status = mC.getInt(mC.getColumnIndexOrThrow(STATUS));
            String timestamp = mC.getString(mC.getColumnIndexOrThrow(TIMESTAMP));
            String token = mC.getString(mC.getColumnIndexOrThrow(TOKEN));
            String device = mC.getString(mC.getColumnIndexOrThrow(DEVICE));
            int user_id = mC.getInt(mC.getColumnIndexOrThrow(USER_ID));

            mC.close();

            return new Session(id, status, timestamp, token, device, user_id);
        }
        return null;
    }

    public Session(int id, int status, String timestamp, String token, String device, int user_id) {

        this.id = id;
        this.status = status;
        this.timestamp = timestamp;
        this.token = token;
        this.device = device;
        this.user_id = user_id;

    }

    public int getUser_id() {
        return user_id;
    }

    public String getDevice() {
        return device;
    }

    public String getToken() {
        return token;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }
}