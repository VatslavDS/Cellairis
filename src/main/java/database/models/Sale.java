package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseAdapter;

/**
 * Created by juanc.jimenez on 30/06/14.
 */
public class Sale extends Table {

    public static final String TABLE = "sale";

    public static final String ID = "id";
    public static final String TIMESTAMP = "timestamp";
    public static final String PAYMENT_TYPE_ID = "payment_type_id";
    public static final String SESSION_ID = "session_id";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String RECEIPT_ID = "receipt_id";

    private int id;
    private String timestamp;
    private String paymentType;
    private Session session;
    private Bitmap receipt;

    public static long insert(Context context, int payment_type_id, int session_id, int customer_id, int receipt_id){

        ContentValues cv = new ContentValues();
        cv.put(PAYMENT_TYPE_ID, payment_type_id);
        cv.put(SESSION_ID, session_id);
        cv.put(CUSTOMER_ID, customer_id);
        cv.put(RECEIPT_ID, receipt_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int update(Context context, int id, int payment_type_id, int session_id, int customer_id, int receipt_id){

        ContentValues cv = new ContentValues();
        if (payment_type_id != -1)
            cv.put(PAYMENT_TYPE_ID, payment_type_id);
        if (session_id != -1)
            cv.put(SESSION_ID, session_id);
        if (customer_id != -1)
            cv.put(CUSTOMER_ID, customer_id);
        if (receipt_id != -1)
            cv.put(RECEIPT_ID, receipt_id);

        return DatabaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + id, null);
    }

    public static int delete(Context context, int id) {

        return DatabaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static Cursor All(Context context) {
        Cursor mc = DatabaseAdapter.getDB(context).rawQuery("select * from "+TABLE,null);
        if(mc !=null)
            mc.moveToFirst();
        return mc;
    }

    public Sale(int id, String timestamp, String paymentType, Session session, Bitmap receipt) {

        this.id = id;
        this.timestamp = timestamp;
        this.paymentType = paymentType;
        this.session = session;
        this.receipt = receipt;
    }

    public int getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Session getSession() {
        return session;
    }

    public Bitmap getReceipt() {
        return receipt;
    }

    public static Sale getSale(Context context, int id) {

        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP));
            int paymentTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(PAYMENT_TYPE_ID));
            String paymentType = PaymentType.getName(context, paymentTypeId);
            int sessionId = cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_ID));
            Session session = Session.getSession(context, sessionId);
            int receiptId = cursor.getInt(cursor.getColumnIndexOrThrow(RECEIPT_ID));
            Bitmap receipt = Receipt.getImage(context, receiptId);

            cursor.close();

            return new Sale(id, timestamp, paymentType, session, receipt);
        }
        return null;
    }

    public static List<Sale> getAll(Context context) {
        List<Sale> sales = new ArrayList<Sale>();
        Cursor cursor = DatabaseAdapter.getDB(context).rawQuery("select * from "+TABLE,null);
        if(cursor !=null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP));
                int paymentTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(PAYMENT_TYPE_ID));
                String paymentType = PaymentType.getName(context, paymentTypeId);
                int sessionId = cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_ID));
                Session session = Session.getSession(context, sessionId);
                int receiptId = cursor.getInt(cursor.getColumnIndexOrThrow(RECEIPT_ID));
                Bitmap receipt = Receipt.getImage(context, receiptId);

                sales.add(new Sale(id, timestamp, paymentType, session, receipt));
            }
            cursor.close();
        }
        return sales;
    }
}
