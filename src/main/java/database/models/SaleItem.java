package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import database.DatabaseAdapter;

/**
 * Created by juanc.jimenez on 30/06/14.
 */
public class SaleItem extends Table {

    public static final String TABLE = "sale_item";

    public static final String ID = "id";
    public static final String QUANTITY = "quantity";
    public static final String PRICE_UNIT = "price_unit";
    public static final String DISCOUNT_PERCENT = "discount_percent";
    public static final String SALE_ID = "sale_id";
    public static final String PRODUCT_ID = "product_id";

    private int id;
    private int quantity;
    private double priceUnit;
    private float discountPercent;
    private int saleId;
    private int productId;

    public SaleItem(int id, int quantity, double priceUnit, float discountPercent, int saleId, int productId) {

        this.id = id;
        this.quantity = quantity;
        this.priceUnit = priceUnit;
        this.discountPercent = discountPercent;
        this.saleId = saleId;
        this.productId = productId;
    }

    public static long insert(Context context, int quantity, double price_unit, float discount_percent, int sale_id, int product_id) {
        ContentValues cv = new ContentValues();
        cv.put(QUANTITY, quantity);
        cv.put(PRICE_UNIT, price_unit);
        cv.put(DISCOUNT_PERCENT, discount_percent);
        cv.put(SALE_ID, sale_id);
        cv.put(PRODUCT_ID, product_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }
    public static Cursor All(Context context) {
        Cursor mc = DatabaseAdapter.getDB(context).rawQuery("select * from "+TABLE,null);
        if(mc !=null)
            mc.moveToFirst();
        return mc;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPriceUnit() {
        return priceUnit;
    }

    public float getDiscountPercent() {
        return discountPercent;
    }

    public int getSaleId() {
        return saleId;
    }

    public int getProductId() {
        return productId;
    }

    public static SaleItem getSaleItem(Context context, int id) {

        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int saleId = cursor.getInt(cursor.getColumnIndexOrThrow(SALE_ID));
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY));
            double itemCost = cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE_UNIT));
            float discount = cursor.getFloat(cursor.getColumnIndexOrThrow(DISCOUNT_PERCENT));

            cursor.close();
            return new SaleItem(id, quantity, itemCost, discount, saleId, productId);
        }
        return null;
    }

    public static ArrayList<SaleItem> getBySaleId(Context context, int saleId) {

        ArrayList<SaleItem> list = new ArrayList<SaleItem>();
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, SALE_ID + "=" + saleId, null, null, null, null);
        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY));
                double itemCost = cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE_UNIT));
                float discount = cursor.getFloat(cursor.getColumnIndexOrThrow(DISCOUNT_PERCENT));

                SaleItem item = new SaleItem(id, quantity, itemCost, discount, saleId, productId);
                list.add(item);

            }
            cursor.close();
        }
        return list;
    }

    public static void deleteByReceivingId(Context context, int saleId) {

        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, SALE_ID + "=" + saleId, null, null, null, null);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                DatabaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
            }
            cursor.close();
        }
    }
}
