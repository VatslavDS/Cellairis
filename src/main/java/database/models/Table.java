package database.models;


import android.content.Context;

import database.DatabaseAdapter;

import static util.LogUtil.makeLogTag;

/*
 * Created by jcenteno on 13/05/14.
 */
public class Table {

    String name;
    private static final String TAG = makeLogTag(Table.class);


    public static void clear(Context context,String table)
    {
        DatabaseAdapter.getDB(context).delete(table,null,null);
    }

    public static void getAll(Context context,String table)
    {
        DatabaseAdapter.getDB(context).execSQL("select * from "+table);
    }

    public static void getById(Context context,String table, String id)
    {
        DatabaseAdapter.getDB(context).execSQL("select * from "+table+" where id = "+id);
    }


}
