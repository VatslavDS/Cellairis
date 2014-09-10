package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.cellairispos.R;

import database.models.Session;

/**
 * Created by juanc.jimenez on 04/09/14.
 */
public class HandleLogout {

    public static void noProfile(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, null);
        ImageView profilePhoto = (ImageView)view.findViewById(R.id.profile_picture);
        TextView current_username = (TextView)view.findViewById(R.id.profile_name);
        current_username.setText("");
        profilePhoto.setImageResource(R.drawable.icon_no_profile);
    }

    public static void noSession(Context context){
        SharedPreferences sp = context.getSharedPreferences("sessions", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("session_id", 0);
        editor.commit();
    }

    public static void noToken(Context context){
        SharedPreferences sp2 = context.getSharedPreferences("token", context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp2.edit();
        editor2.putString("access_token", "");
        editor2.commit();
    }
}
