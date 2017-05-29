package com.pomegro.android.entrance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pomegro.android.MainActivity;
import com.pomegro.android.R;

public class SignupFragment1 extends Fragment {

    private Context context;
    private SharedPreferences preferences;

    private View layout;
    private EditText usernameField, pw1Field, pw2Field, emailField;
    private ProgressDialog progressDialog;

    private String username, pw1, pw2, email;

    public static SignupFragment1 newInstance() {
        SignupFragment1 fragment = new SignupFragment1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup1, container, false);


        layout = view.findViewById(R.id.mainLayout1);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GrandActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });*/

        usernameField = (EditText) view.findViewById(R.id.editTextUsername);
        pw1Field = (EditText) view.findViewById(R.id.editTextPassword);
        pw2Field = (EditText) view.findViewById(R.id.editTextPasswordA);
        emailField = (EditText) view.findViewById(R.id.editTextMail);

        Button buttonSignUp = (Button) view.findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                //signin();
                Snackbar snackbar = Snackbar
                        .make(layout, getActivity().getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment newFragment = new TestFragment();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                snackbar.show();
            }
        });
        return view;
    }

}
