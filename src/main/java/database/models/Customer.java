package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseAdapter;

/**
 * Created by juanc.jimenez on 30/06/14.
 */
public class Customer extends Table {

    public static final String TABLE = "customer";

    public static final String ID = "id";
    public static final String ACCOUNT = "account";
    public static final String TAXABLE = "taxable";
    public static final String PEOPLE_ID = "people_id";

    private int id;
    private int account;
    private boolean taxable;
    private People info;

    public Customer(int account, boolean taxable, People info) {

        this.account = account;
        this.taxable = taxable;
        this.info = info;
    }

    public static long insert(Context context, int account, int taxable, int people_id) {
        ContentValues cv = new ContentValues();
        cv.put(ACCOUNT, account);
        cv.put(TAXABLE, taxable);
        cv.put(PEOPLE_ID, people_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {
        return DatabaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static Customer getCustomer(Context context, int id) {

        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            int account = cursor.getInt(cursor.getColumnIndexOrThrow(ACCOUNT));
            int taxableId = cursor.getInt(cursor.getColumnIndexOrThrow(TAXABLE));
            boolean taxable = taxableId == 0;
            int peopleId = cursor.getInt(cursor.getColumnIndexOrThrow(PEOPLE_ID));
            People info = People.getPeople(context, peopleId);

            return new Customer(account, taxable, info);
        }
        return null;
    }

    public static List<Customer> getAll(Context context) {

        List<Customer> customers = new ArrayList<Customer>();
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, null, null, null, null, null);

        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                int account = cursor.getInt(cursor.getColumnIndexOrThrow(ACCOUNT));
                int taxableId = cursor.getInt(cursor.getColumnIndexOrThrow(TAXABLE));
                boolean taxable = taxableId == 1;
                int peopleId = cursor.getInt(cursor.getColumnIndexOrThrow(PEOPLE_ID));
                People info = People.getPeople(context, peopleId);

                customers.add(new Customer(account, taxable, info));
            }
        }
        return customers;
    }

    public int getId() {
        return id;
    }

    public int getAccount() {
        return account;
    }

    public boolean getTaxable() {
        return taxable;
    }

    public People getInfo() {
        return info;
    }
}
