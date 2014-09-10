package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import database.DatabaseAdapter;

/**
 * Created by jcenteno on 09/06/14.
 */
public class ProductCategory extends Table {

    public static final String TABLE = "product_category";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRODUCT_CATEGORY_ID = "product_category_id";


    public static long insert(Context context, String name, int product_category_id) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(PRODUCT_CATEGORY_ID, product_category_id);

        return DatabaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int getId(Context context, String name) {

        int id = 0;
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, NAME + "=?", new String[]{name}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            cursor.close();
        }
        return id;
    }

    public static ArrayList<String> getCategoriesForParent(Context context, int parentId) {

        ArrayList<String> categoriesList = new ArrayList<String>();
        Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, PRODUCT_CATEGORY_ID + "=" + parentId, null, null, null, NAME);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                categoriesList.add(cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
            }
            cursor.close();
        }
        return categoriesList;
    }

    public static String getProductCategory(Context context, int lowestCategoryId) {
        ArrayList<String> categoriesList = new ArrayList<String>();
        String category = "";
        int parentId = lowestCategoryId;

        do {
            Cursor cursor = DatabaseAdapter.getDB(context).query(TABLE, null, ID + "=" + parentId, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                categoriesList.add(0, cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                parentId = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY_ID));
                cursor.close();
            }
        }while (parentId != 0);

        for (String name : categoriesList)
            category += name + "/";

        return category;
    }
}
