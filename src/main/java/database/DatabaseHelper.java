package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import database.models.Customer;
import database.models.Module;
import database.models.PaymentType;
import database.models.People;
import database.models.Permission;
import database.models.Product;
import database.models.ProductBrand;
import database.models.ProductCategory;
import database.models.ProductPic;
import database.models.Profile;
import database.models.Receipt;
import database.models.RecordActions;
import database.models.Sale;
import database.models.SaleItem;
import database.models.Session;
import database.models.User;

/**
 * Created by jcenteno on 12/05/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Tracker?? what is the name of the database
    private static final String DATABASE_NAME = "tracker.db";
    private static final String TAG = "DatabaseHelper";

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int VER_1 = 23;  // 0.23
    private static final int DATABASE_VERSION = VER_1;
    public static final int NOT_UPDATE = -1;
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE "+ User.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"username varchar(250) NOT NULL,"
                +"password varchar(250) NOT NULL,"
                +"people_id integer NOT NULL,"
                +"FOREIGN KEY (people_id) REFERENCES profile (id))");

        db.execSQL("CREATE TABLE "+ Profile.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ RecordActions.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"data text NOT NULL,"
                +"name_webservice varchar(250) NOT NULL,"
                +"user_id integer NOT NULL,"
                +"FOREIGN KEY (user_id) REFERENCES user (id))");

        db.execSQL("CREATE TABLE "+Product.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"barcode varchar(250) UNIQUE,"
                +"name varchar(250) NOT NULL,"
                +"description text,"
                +"unit_price integer,"
                +"cost_price integer,"
                +"tax float,"
                +"stock integer,"
                +"stock_central integer,"
                +"product_category_id integer NOT NULL,"
                +"product_brand_id integer NOT NULL,"
                +"FOREIGN KEY (product_category_id) REFERENCES product_category (id),"
                +"FOREIGN KEY (product_brand_id) REFERENCES product_brand (id))");

        db.execSQL("CREATE TABLE "+ ProductBrand.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ ProductCategory.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"name varchar(250) NOT NULL,"
                +"product_category_id integer,"
                +"FOREIGN KEY (product_category_id) REFERENCES product_category (id))");

        db.execSQL("CREATE TABLE "+ ProductPic.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"source text NOT NULL,"
                +"product_id integer NOT NULL,"
                +"main integer NOT NULL,"
                +"FOREIGN KEY (product_id) REFERENCES product (id))");

        db.execSQL("CREATE TABLE "+ Customer.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"account integer NOT NULL,"
                +"taxable integer NOT NULL,"
                +"people_id integer NOT NULL,"
                +"FOREIGN KEY (people_id) REFERENCES people (id))");

        db.execSQL("CREATE TABLE "+ PaymentType.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Receipt.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY,"
                +"image text NOT NULL)");

        db.execSQL("CREATE TABLE "+ People.TABLE +" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    first_name varchar(250) NOT NULL," +
                "    last_name varchar(250) NOT NULL," +
                "    email varchar(250) NOT NULL," +
                "    rfc varchar(250)," +
                "    phone_number varchar(250)," +
                "    address_1 varchar(250)," +
                "    address_2 varchar(250)," +
                "    city varchar(250)," +
                "    state varchar(250)," +
                "    country varchar(250)," +
                "    comments varchar(250)," +
                "    zip_code integer," +
                "    photo blob" +
                ")");

        db.execSQL("CREATE TABLE "+ Session.TABLE+" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    status integer NOT NULL," +
                "    timestamp datetime NOT NULL DEFAULT (datetime('now','localtime'))," +
                "    token varchar(250) NOT NULL," +
                "    device varchar(250) NOT NULL," +
                "    user_id integer NOT NULL," +
                "    FOREIGN KEY (user_id) REFERENCES user (id)" +
                ")");

        db.execSQL("CREATE TABLE "+ Sale.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"timestamp datetime DEFAULT (datetime('now','localtime')),"
                +"payment_type_id integer NOT NULL,"
                +"session_id integer NOT NULL,"
                +"customer_id integer NOT NULL,"
                +"receipt_id integer NOT NULL,"
                +"FOREIGN KEY (payment_type_id) REFERENCES payment_type (id),"
                +"FOREIGN KEY (session_id) REFERENCES session (id),"
                +"FOREIGN KEY (customer_id) REFERENCES customer (id),"
                +"FOREIGN KEY (receipt_id) REFERENCES receipt (id))");

        db.execSQL("CREATE TABLE "+ SaleItem.TABLE +" (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "quantity integer NOT NULL," +
                "price_unit double NOT NULL," +
                "discount_percent float," +
                "sale_id integer NOT NULL," +
                "product_id integer NOT NULL," +
                "FOREIGN KEY (sale_id) REFERENCES sale (id)," +
                "FOREIGN KEY (product_id) REFERENCES product (id))");


        db.execSQL("CREATE TABLE " + Permission.TABLE + " (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "profile_id integer NOT NULL," +
                "module_id integer NOT NULL," +
                "FOREIGN KEY (profile_id) REFERENCES PROFILE (id)," +
                "FOREIGN KEY (module_id) REFERENCES MODULE (id))");

        db.execSQL("CREATE TABLE " + Module.TABLE + " (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name varchar(50) NOT NULL)");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOTE: This switch statement is designed to handle cascading database
        // updates, starting at the current version and falling through to all
        // future upgrade cases. Only use "break;" when you want to drop and
        // recreate the entire database.
        switch (oldVersion) {
            case VER_1:
                Log.i(TAG, "Database Ver " + oldVersion);
        }

        Log.d(TAG, "after upgrade logic, at version " + oldVersion);
        if (oldVersion != DATABASE_VERSION) {
            Log.w(TAG, "Destroying old data during upgrade");
            db.execSQL("DROP TABLE IF EXISTS " + Customer.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PaymentType.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + People.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Product.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ProductBrand.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ProductCategory.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ProductPic.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Profile.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Receipt.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + RecordActions.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Sale.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SaleItem.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Session.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Module.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
            onCreate(db);
        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
