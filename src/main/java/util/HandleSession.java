package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import database.models.Session;

/**
 * Created by juanc.jimenez on 29/08/14.
 */
public class HandleSession {

    private String status;
    private String token;
    private int user_id;
    private String username;


    public HandleSession(String status, String token, int user_id, String username){
        this.status = status;
        this.token = token;
        this.user_id = user_id;
        this.username = username;
    }

    public static boolean startSession(Context context, int status, String token, int user_id, String username){

        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Long id_session = Session.insert(context, status, token, android_id, user_id);

        SharedPreferences sp = context.getSharedPreferences("sessions", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("session_id", id_session);
        editor.commit();

        SharedPreferences sp2 = context.getSharedPreferences("token", context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp2.edit();
        editor2.putString("access_token", token);
        editor2.commit();

        SharedPreferences sp3 = context.getSharedPreferences("credentials", context.MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sp3.edit();
        editor3.putString("username", username);
        editor3.commit();
        return true;
    }

    public static long getCurrentSession(Context context){
        SharedPreferences sp = context.getSharedPreferences("sessions", context.MODE_PRIVATE);
        long id = sp.getLong("session_id", 0);
        return id;
    }

    public static String getToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("token", context.MODE_PRIVATE);
        String token = sp.getString("access_token", "");
        return  token;
    }

    public static String getCurrentUser(Context context){
        SharedPreferences sp = context.getSharedPreferences("credentials", context.MODE_PRIVATE);
        String username = sp.getString("credentials", "");
        return username;
    }
}
