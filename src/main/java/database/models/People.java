package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import database.DatabaseAdapter;

import static util.LogUtil.makeLogTag;

/**
 * Created by dmolinero on 27/06/14.
 */
public class People extends Table {

    private static final String TAG = makeLogTag(People.class);
    //Table Name
    public static final String TABLE = "people";

    public static final String ID = "id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String RFC = "rfc";
    public static final String PHONE = "phone_number";
    public static final String ADDRESS1 = "address_1";
    public static final String ADDRESS2 = "address_2";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String COMMENTS = "comments";
    public static final String CP = "zip_code";
    public static final String PHOTO = "photo";

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String rfc;
    private String phone;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String comments;
    private String zipCode;

    public People(int id, String firstName, String lastName, String email, String rfc, String phone,
                  String address1, String address2, String city, String state, String country, String comments, String zipCode){

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.rfc = rfc;
        this.phone = phone;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.comments = comments;
        this.zipCode = zipCode;
    }

    public static long insert(Context context, String first_name, String last_name, String email, String rfc, String phone,
                              String address1, String address2, String city, String state, String country, String comments, String cp,
                              String photo) {
        ContentValues cv = new ContentValues();
        cv.put(FIRST_NAME, first_name);
        cv.put(LAST_NAME, last_name);
        cv.put(EMAIL, email);
        cv.put(RFC, rfc);
        cv.put(PHONE, phone);
        cv.put(ADDRESS1, address1);
        cv.put(ADDRESS2, address2);
        cv.put(CITY, city);
        cv.put(STATE, state);
        cv.put(COUNTRY, country);
        cv.put(COMMENTS, comments);
        cv.put(CP, cp);
        cv.put(PHOTO, photo);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int update(Context context, int id, String first_name, String last_name, String email, String rfc, String phone,
                              String address1, String address2, String city, String state, String country, String comments, String cp,
                              String photo) {

        ContentValues cv = new ContentValues();

        if (first_name != null)
            cv.put(FIRST_NAME, first_name);
        if (last_name != null)
            cv.put(LAST_NAME, last_name);
        if (email != null)
            cv.put(EMAIL, email);
        if (rfc != null)
            cv.put(RFC, rfc);
        if (phone != null)
            cv.put(PHONE, phone);
        if (address1 != null)
            cv.put(ADDRESS1, address1);
        if (address2 != null)
            cv.put(ADDRESS2, address2);
        if (city != null)
            cv.put(CITY, city);
        if (state != null)
            cv.put(STATE, state);
        if (country != null)
            cv.put(COUNTRY, country);
        if (comments != null)
            cv.put(COMMENTS, comments);
        if (cp != null)
            cv.put(CP, cp);
        if (photo != null)
            cv.put(PHOTO, photo);

        return DatabaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + id, null);
    }

    public static People getPeople(Context context, int id){

        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(LAST_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL));
            String rfc = cursor.getString(cursor.getColumnIndexOrThrow(RFC));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(PHONE));
            String address1 = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS1));
            String address2 = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS2));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(CITY));
            String state = cursor.getString(cursor.getColumnIndexOrThrow(STATE));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(COUNTRY));
            String comments = cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS));
            String zipCode = cursor.getString(cursor.getColumnIndexOrThrow(CP));

            return new People(id, firstName, lastName, email, rfc, phone, address1, address2, city, state, country, comments, zipCode);
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRfc() {
        return rfc;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getComments() {
        return comments;
    }

    public String getZipCode() {
        return zipCode;
    }
}
