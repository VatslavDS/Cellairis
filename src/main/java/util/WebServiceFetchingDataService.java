package util;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.mx.cellairispos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileLockInterruptionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import database.models.Product;
import database.models.ProductBrand;
import database.models.ProductCategory;
import database.models.ProductPic;

/**
 * Created by juanc.jimenez on 26/08/14.
 */
public class WebServiceFetchingDataService extends Service{

    private String TAG_JSON = "JS0N_REQUEST";
    private String TAG_IMAGE = "IMAGE_REQUET";
    private String TAG_SOURCE = "SOURCE OF IMAGES";

    //Here the url, for now dummy url
    private String url_get = "http://54.187.28.61/APIcellairis/v1/products";
    private String url_post = "http://54.187.28.61/APIcellairis/v1/product";



    //Products fields
    String barcode, name, description, brand;
    static Double unit_price, cost_price;

    float tax;
    static int stock, stock_central, product_category_id, product_brand_id;

    //Products_category
    static String name_product_category;
    static int product_category;

    //Product_PIC
    static String source;
    static int main;

    //Path of image in external storage
    static String path;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Here is where we make http request

        RetryPolicy policy = new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url_get, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray products_array = response.getJSONArray("products");
                            for(int i = 0; i < products_array.length(); i++){
                                JSONObject current = products_array.getJSONObject(i);
                                //sku = current.getString("sku");
                                barcode = current.getString("sku");
                                name = current.getString("name");
                                description = current.getString("description");
                                brand = current.getString("brand");
                                //unit_price = current.getDouble("unit_price");
                                try{
                                    cost_price = current.getDouble("pricelist");
                                }catch(Exception e){
                                    cost_price = 0.0;
                                }
                                tax = (float)current.getDouble("tax");
                                stock = current.getInt("quantity");
                                //stock_central= current.getInt("stock_central");
                                //product_category_id = current.getInt("product_category_id");
                                //product_brand_id = current.getInt("product_brand_id");
                                //name_product_category = product_category_obj.getString("name_product_category");
                                //product_category = product_category_obj.getInt("product_category");

                                JSONArray images = current.getJSONArray("images");
                                String source = images.getJSONObject(0).getString("url");
                                //main = product_pic_obj.getString("main");

                                //JSONObject product_brand_obj = current.getJSONObject("product_brand");
                                //name_product_category = product_brand_obj.getString("name");
                                long current_product_brand = ProductBrand.insert(getApplicationContext(), brand);
                                long current_product = Product.insert(getApplicationContext(), barcode, name, description, null, cost_price, tax, stock, 0, 0, (int)current_product_brand);
                                if(current_product != -1 && current_product_brand != -1){
                                    saveImageFromWebService(source, (int)current_product);
                                    Log.v("SAVED A SOURCE", " The path " +  "CORRECT");
                                }
                                //public static long insert(Context context, String barcode, String name, String description, Double unit_price, Double cost_price, float tax, int stock, int stock_central, int product_category_id, int product_brand_id) {
                                //ProductBrand.insert(getApplication(), name_product_category);
                                //ProductCategory.insert(getApplicationContext(), name_product_category, product_category);

                            }

                            //Product.insert String barcode, String name, String description, Double unit_price, Double cost_price, float tax, int stock, int stock_central, int product_category_id, int product_brand_id

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Here we handling the 404 response code
                try{
                    Log.v("ON ERROR IN THE REQUEST", error.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }){
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError{
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        JsonObjectRequest jsonPostRequest = new JsonObjectRequest(Request.Method.POST,
                url_post, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }
                }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Here we handling the 404 response code or other errors
                    try{
                        Log.v("ON ERROR IN THE REQUEST", error.toString());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                }){
                    @Override
                    public HashMap<String, String> getHeaders() throws AuthFailureError{
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }
        };

        //Policy is the timeout
        jsonObjReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG_JSON);

        //SECOND REQUEST

        //String image_url = "http://icdn.pro/images/es/a/n/animales-oso-panda-icono-9546-48.png";
        // Retrieves an image specified by the URL, displays it in the UI.

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    //This method crate a request then attach it to queue for request for each image linked to product
    //Also use scale the bitmap retrieving of webservice and it'll save to external storage
    private void saveImageFromWebService(String url, final int current_product) {
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                path = saveToSD(scaled);
                Long current_image = ProductPic.insert(getApplicationContext(), path, current_product, 0);
                Log.v("CURRENT_IMAGE", "ELEMENT "+ current_image);
            }
        }, 0, 0, null, null);
        AppController.getInstance().addToRequestQueue(ir, TAG_IMAGE);
    }

    //Method for save an image into external file system
    public String saveToSD(Bitmap outputImage){

        //THis create a directory
        String name_millis = Long.toString(System.currentTimeMillis());
        String directory = Environment.getExternalStorageDirectory() + "/.Cellairis/";
        File storagePath = new File(directory);
        storagePath.mkdirs();

        File myImage = new File(storagePath, name_millis + ".jpg");

        try {
            FileOutputStream out = new FileOutputStream(myImage);
            outputImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/.Cellairis/" + name_millis + ".jpg";
    }


    //Method from android doc.
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
