package com.pomegro.android.entrance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pomegro.android.R;

/**
 * Created by Baris on 8.01.2016.
 */
public class TestFragment extends Fragment {

    // Store instance variables
    private String title;
    private Button button, button2;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = "asd";
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ex, container, false);
        button = (Button) view.findViewById(R.id.button);
        button2 = (Button) view.findViewById(R.id.button2);
        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
        tvLabel.setText("testt");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // closing the keyboard when login button clicked
                button2.setVisibility(View.GONE);
            }
        });
        return view;
    }
}
