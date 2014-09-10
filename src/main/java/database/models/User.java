package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DatabaseAdapter;

import static util.LogUtil.makeLogTag;

/**
 * Created by jcenteno on 13/05/14.
 */
public class User extends Table {

    private static final String TAG = makeLogTag(User.class);

    //Table Name
    public static final String TABLE = "user";
    //***Datos del Usuario
    public static final String ID = "id";
    public static final String EMAIL = "username";
    public static final String PASSWORD = "password";
    public static final String ID_PROFILE = "profile_id";
    public static final String ID_PEOPLE = "people_id";

    private int id;
    private String email;
    private String password;
    private String profile;
    private People people;


    public User(int id, String email, String password, String profile, People people) {

        this.id = id;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.people = people;
    }

    public static long insert(Context appContext, String username, String password, int id_profile, int id_people) {
        ContentValues cv = new ContentValues();
        cv.put(EMAIL, username);
        cv.put(PASSWORD, password);
        cv.put(ID_PROFILE, id_profile);
        cv.put(ID_PEOPLE, id_people);

        return DatabaseAdapter.getDB(appContext).insert(TABLE, null, cv);
    }

    public static Cursor getAll(Context context) {
        Cursor mC = null;
        mC = DatabaseAdapter.getDB(context).query(TABLE, null, null, null, null, null, null);
        if (mC != null) {
            mC.moveToFirst();
        }
        return mC;
    }

    public static Cursor getUser(Context context, String id) {
        Cursor mC = null;
        mC = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=?", new String[]{id}, null, null, null);
        if (mC != null) {
            mC.moveToFirst();
        }
        return mC;
    }

    public static User getUser(Context context, int id) {
        Cursor mC = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (mC != null && mC.moveToFirst()) {

            String userName = mC.getString(mC.getColumnIndexOrThrow(EMAIL));
            String password = mC.getString(mC.getColumnIndexOrThrow(PASSWORD));
            int profileId = mC.getInt(mC.getColumnIndexOrThrow(ID_PROFILE));
            String profile = Profile.getName(context, profileId);
            int peopleId = mC.getInt(mC.getColumnIndexOrThrow(ID_PEOPLE));
            People people = People.getPeople(context, peopleId);
            mC.close();

            return new User(id, userName, password, profile, people);
        }
        return null;
    }

    public static boolean getSession(Context context) {
        Cursor mC = null;
        String last_update = null;
        mC = DatabaseAdapter.getDB(context).rawQuery(
                "Select * from user where session = 1", null);
        if (mC != null && mC.getCount()>0) {
            mC.moveToFirst();
            return true;
        }
        return false;
    }

    public static User activeUser(Context context) {

        int userId = Session.getSessionActive(context).getId();

        return getUser(context, userId);
    }

    public static Cursor getUserForEmail(Context context, String email) {
        Cursor mC;
        mC = DatabaseAdapter.getDB(context).query(TABLE, null, EMAIL + "=?", new String[]{email}, null, null, null);
        if (mC != null) {
            mC.moveToFirst();
        }
        return mC;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile() {
        return profile;
    }

    public People getPeople() {
        return people;
    }
}
