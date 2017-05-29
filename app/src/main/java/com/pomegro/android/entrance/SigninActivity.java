package com.pomegro.android.entrance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.pomegro.android.R;
import com.pomegro.android.utility.VolleySingleton;
import com.pomegro.android.newsfeed.GrandActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SigninActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences preferences;

    private View layout;
    private EditText usernameField, passwordField;
    private CheckBox checkBox;
    private ProgressDialog progressDialog;

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        context = this;
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        layout = findViewById(R.id.mainLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, com.pomegro.android.MainActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        usernameField = (EditText)findViewById(R.id.editTextUsername);
        passwordField = (EditText)findViewById(R.id.editTextPassword);
        checkBox = (CheckBox)findViewById(R.id.cBoxRemember);
        checkBox.setChecked(true);

        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                signin();
            }
        });

    }

    private void signin() {
        if(preferences.getBoolean("isRegistered", false)) {
            username = preferences.getString("username", "");
            password = preferences.getString("password", "");
            try {
                signinTask(true);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            username = usernameField.getText().toString();
            password = passwordField.getText().toString();
            if(checkBox.isChecked()) {
                try {
                    signinTask(true);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                try {
                    signinTask(false);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void signinTask(final boolean isRemember) throws JSONException {

        progressDialog = ProgressDialog.show(context, getResources().getString(R.string.progDialog1), getResources().getString(R.string.progDialog2), true, true);

        JSONObject postObj = new JSONObject();

        postObj.put("user", username);
        postObj.put("pass", password);

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, "http://www.berkugudur.com/narr3/query.php?action=signin", postObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                SharedPreferences.Editor editor = preferences.edit();

                String id = null;
                try {
                    id = result.getJSONObject("entrance").getString("id");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(id.equals("0")) { // wrong username or password
                    if(preferences.getBoolean("isRegistered", false)) {
                        editor.putBoolean("isRegistered", false); // if the password had changed, system orientate user to sign in screen again.
                        editor.apply();

                        Intent intent = new Intent(context, SigninActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        Snackbar.make(layout, context.getResources().getString(R.string.siresult1), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else if(id.equals("a")) { // database connection error
                    Snackbar snackbar = Snackbar
                            .make(layout, context.getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        signinTask(true);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    snackbar.show();
                } else { // signed in successfully
                    if(preferences.getBoolean("isRegistered", false)) { } else { // saving the values to make easier signing in again
                        if(isRemember) {
                            editor.putBoolean("isRegistered", true);
                            editor.putString("username", username);
                            editor.putString("password", password);
                            editor.putString("refreshdate", "0");
                            editor.putString("lastpolldate", "0");
                            editor.apply();
                        }
                    }

                    editor.putString("userid", id);
                    editor.apply();

                    Snackbar.make(layout, context.getResources().getString(R.string.siresult2), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Intent intent = new Intent(context, GrandActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(layout, context.getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressDialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                }

                if (error instanceof TimeoutError) {
                    Log.e("Volley", "TimeoutError: " + error.toString());
                }else if(error instanceof NoConnectionError){
                    Log.e("Volley", "NoConnectionError: " + error.toString());
                } else if (error instanceof AuthFailureError) {
                    Log.e("Volley", "AuthFailureError: " + error.toString());
                } else if (error instanceof ServerError) {
                    Log.e("Volley", "ServerError: " + error.toString());
                } else if (error instanceof NetworkError) {
                    Log.e("Volley", "NetworkError: " + error.toString());
                } else if (error instanceof ParseError) {
                    Log.e("Volley", "ParseError: " + error.toString());
                }
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(jr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_newsfeed, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, com.pomegro.android.MainActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();

        super.onBackPressed();
    }

}
