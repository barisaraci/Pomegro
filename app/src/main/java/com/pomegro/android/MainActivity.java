package com.pomegro.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.felipecsl.gifimageview.library.GifImageView;
import com.pomegro.android.entrance.SigninActivity;
import com.pomegro.android.entrance.SignupActivity;
import com.pomegro.android.newsfeed.GrandActivity;
import com.pomegro.android.utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences preferences;

    private GifImageView gifView;
    private View layout, progressView;
    private Button buttonSignIn, buttonSignUp;

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        layout = findViewById(R.id.mainLayout);

        try {
            InputStream is = getAssets().open("entrance.gif");
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();

            gifView = (GifImageView) findViewById(R.id.gifImageView);
            gifView.setBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, SigninActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignupActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        progressView = findViewById(R.id.login_progress);

        if(preferences.getBoolean("isRegistered", false)) {
            username = preferences.getString("username", "");
            password = preferences.getString("password", "");
            showProgress(true);
            try {
                signinTask(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void signinTask(final boolean isRemember) throws JSONException {

        showProgress(true);

        JSONObject postObj = new JSONObject();

        postObj.put("user", username);
        postObj.put("pass", password);

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, "http://www.berkugudur.com/narr/query.php?action=signin", postObj, new Response.Listener<JSONObject>() {
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
                        showProgress(false);
                    }
                } else if(id.equals("a")) { // database connection error
                    Snackbar snackbar = Snackbar
                            .make(layout, context.getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showProgress(false);
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

                    showProgress(false);

                    Intent intent = new Intent(context, GrandActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make(layout, context.getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showProgress(false);
                                try {
                                    signinTask(true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                snackbar.show();
                showProgress(false);

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            buttonSignIn.setVisibility(show ? View.GONE : View.VISIBLE);
            buttonSignIn.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    buttonSignIn.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            buttonSignUp.setVisibility(show ? View.GONE : View.VISIBLE);
            buttonSignUp.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    buttonSignUp.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            buttonSignIn.setVisibility(show ? View.GONE : View.VISIBLE);
            buttonSignUp.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gifView.startAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gifView.stopAnimation();
    }

}