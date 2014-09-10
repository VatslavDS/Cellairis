package com.mx.cellairispos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.provider.Settings.Secure;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.models.People;
import database.models.Profile;
import database.models.Session;
import database.models.User;
import util.AppController;
import util.Utilities;
import util.HandleSession;

import static util.LogUtil.makeLogTag;

/**
 * Created by juanc.jimenez on 15/08/14.
 */
public class LoginActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener{

    //UI elements
    private EditText user, password;
    private Button login;

    //TAG
    final String TAG_OWN_CLASS = "LOGIN";

    //login fields
    public String email;
    public String pass;
    public String username;

    //Session
    private Long session;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        //If we have session and token its better to redirect to activity, the session is active
        token = HandleSession.getToken(this);
        session = HandleSession.getCurrentSession(this);
        if(session != 0 && !token.isEmpty()){
            startActivity(new Intent(this, MainActivity.class));
        }
        */


        //Inflates the view and initializes the UI elements
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(this);
        password.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {
        attemptLogin();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        //If the action button pressed is IME_ACTION_DONE, click the login button
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            login.performClick();
            return true;
        }
        return false;
    }

    public void attemptLogin() {

        //Clear errors on the field in case there were some
        user.setError(null);
        password.setError(null);

        //Read the inputs
        email = user.getText().toString();
        pass = password.getText().toString();
        boolean error = false;

        //If the password is empty or is invalid throw an error
        if (TextUtils.isEmpty(pass) || !isPasswordValid(pass)) {
            password.setError(getString(R.string.error_invalid_password));
            error = true;
        }
        //If the user is empty throw and error
        if (TextUtils.isEmpty(email)) {
            user.setError(getString(R.string.error_field_required));
            error = true;
        }
        //If the user is invalid throw an error
        if (isUserValid(email)) {
            user.setError(getString(R.string.error_invalid_email));
            error = true;
        }

        //If no error was found, simulate login
        if (!error)
            //here we need to pass in form of string the username and password
            new UserLoginTask().execute(new String[]{email, pass});
    }

    private boolean isUserValid(String username) {
        return !username.equals("vf_plaza");
    }


    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }



    private void finishLogin() {

        //Clear the fields and starts the MainActivity
        password.setText("");
        user.setText("");
    }

    //Saving the current context of our activity
    public Context getCurrentContext(){
        return getApplicationContext();
    }

    //And here we expect to recieve <String, Void, String> first param to doInBackGroud, second onPre, third onPost
    public class UserLoginTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {

            //THis could be work with some UX of the Dialog
            super.onPreExecute();

            //Hides keyboard
            Utilities.hideKeyboard(LoginActivity.this, password);

            //Creates a dialog
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setMessage(getString(R.string.logging_in));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = "http://54.187.219.128/vodafone/api.php";
            //We need to retrieve the params[0] and params[1] for email and password
            httpRequestSession(url, params[0], params[1]);
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {

            //If all goes well dismiss the dialog and finish the login process
            if (success) {
                dialog.dismiss();
                finishLogin();
            } else {
                password.setError(getString(R.string.error_incorrect_password));
                password.requestFocus();
            }
        }
    }

    public void httpRequestSession(String url, final String email, final String password){
         //Here is where we make http request
         String TAG_JSON = makeLogTag(LoginActivity.class);
         boolean isLogginIn = false;

        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("respuesta", "r:" + response);
                boolean success = false;
                String textResponse = "";
                String textToken = "";

                //user: vf_plaza

                //Data received, parse it to JSONObject and obtain fields
                try {
                    JSONObject resp = new JSONObject(response);
                    success = resp.getBoolean("success");
                    textResponse = resp.getString("resp");
                    textToken = resp.getString("token");
                    username = resp.getString("username");

                    int id_profile = (int)Profile.insert(getApplicationContext(), email);
                    int id_user = (int)User.insert(getApplicationContext(), email, password, id_profile, 0);


                    //Updating the info of the drawer
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.fragment_navigation_drawer, null);

                    TextView tx = (TextView)view.findViewById(R.id.profile_name);
                    tx.setText(username);

                    HandleSession.startSession(getApplicationContext(), 1, textToken, id_user, username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!success) {
                    Toast.makeText(getApplicationContext(), textResponse, Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Log.e("Error: ", "e:" +error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request", "login");
                params.put("user", email);
                params.put("password", pass);
                return params;
            }
        };

         AppController.getInstance().addToRequestQueue(req, TAG_JSON);
     }

}
