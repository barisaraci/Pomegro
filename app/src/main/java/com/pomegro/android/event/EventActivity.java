package com.pomegro.android.event;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pomegro.android.R;
import com.pomegro.android.newsfeed.GrandActivity;
import com.pomegro.android.utility.ImageDetailsActivity;
import com.pomegro.android.utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity {

    private Context context;
    private Event event;

    private View layout;
    private LinearLayout layoutMain;
    private TextView textViewName, textViewTopic, textViewContent, textViewInterests, textViewLocation, textViewTime, textViewLeftTime, textViewParticipantNumber, textViewInformation, textViewTitle;
    private Button buttonRt, buttonStar;
    private CircularImageView avatar;
    private HorizontalScrollView scrollViewImages;

    private NetworkImageView niv1, niv2, niv3, niv4, niv5, niv6;

    private ArrayList<NetworkImageView> nivList = new ArrayList<>();

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        context = this;
        layout = findViewById(R.id.mainLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        layoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);

        scrollViewImages = (HorizontalScrollView) findViewById(R.id.scrollViewImages);

        avatar = (CircularImageView) findViewById(R.id.circularImageViewAvatar);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTopic = (TextView) findViewById(R.id.textViewTopic);
        textViewContent = (TextView) findViewById(R.id.textViewContent);
        textViewInterests = (TextView) findViewById(R.id.textViewInterests);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewLeftTime = (TextView) findViewById(R.id.textViewLeftTime);
        textViewParticipantNumber = (TextView) findViewById(R.id.textViewParticipantNumber);
        textViewInformation = (TextView) findViewById(R.id.textViewInfContent);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);

        niv1 = (NetworkImageView) findViewById(R.id.networkImageView1);
        niv2 = (NetworkImageView) findViewById(R.id.networkImageView2);
        niv3 = (NetworkImageView) findViewById(R.id.networkImageView3);
        niv4 = (NetworkImageView) findViewById(R.id.networkImageView4);
        niv5 = (NetworkImageView) findViewById(R.id.networkImageView5);
        niv6 = (NetworkImageView) findViewById(R.id.networkImageView6);

        niv1.setVisibility(View.GONE); niv2.setVisibility(View.GONE); niv3.setVisibility(View.GONE); niv4.setVisibility(View.GONE); niv5.setVisibility(View.GONE); niv6.setVisibility(View.GONE);

        nivList.add(niv1); nivList.add(niv2); nivList.add(niv3); nivList.add(niv4); nivList.add(niv5); nivList.add(niv6);

        buttonRt = (Button) findViewById(R.id.buttonRt);
        buttonStar = (Button) findViewById(R.id.buttonStar);

        event = getIntent().getParcelableExtra("event");

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //collapsingToolbarLayout.setTitle("Pomegro");
                    textViewTitle.setText(event.getTopic() + "\r\n" + context.getResources().getString(R.string.ownerName) + " " + name);
                    isShow = true;
                } else if (isShow) {
                    //collapsingToolbarLayout.setTitle("");
                    textViewTitle.setText("");
                    isShow = false;
                }
            }
        });

        textViewName.setText(event.getName());
        textViewTopic.setText(event.getTopic());
        textViewLocation.setText(event.getLocation());
        textViewParticipantNumber.setText(event.getCurAtt() + " / " + event.getMaxAtt());
        textViewContent.setText(event.getContent());

        StringBuilder sb = new StringBuilder(event.getInterests());

        int j = 0;
        while(j < sb.length()) {
            if(sb.charAt(j) == '[' || sb.charAt(j) == ']' || sb.charAt(j) == '"') {
                sb.deleteCharAt(j);
                j--;
            }
            j++;
        }

        String interests[] = sb.toString().split(",");

        textViewInterests.setText("");
        for(int i = 0; i < interests.length; i++) {
            textViewInterests.setText(textViewInterests.getText() + "#" + interests[i] + " ");
        }

        if(event.getTimeLeft() > 7 * 24 * 60) { textViewLeftTime.setText("( " + event.getTimeLeft() / (7 * 24 * 60) + context.getResources().getString(R.string.timeWeek) + " " + (event.getTimeLeft() / (24 * 60)) % 7 + context.getResources().getString(R.string.timeDay) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
        else if(event.getTimeLeft() > 24 * 60) { textViewLeftTime.setText("( " + event.getTimeLeft() / (24 * 60) + context.getResources().getString(R.string.timeDay) + " " + (event.getTimeLeft() / 60) % 24 + context.getResources().getString(R.string.timeHour) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
        else if(event.getTimeLeft() > 60) { textViewLeftTime.setText("( " + event.getTimeLeft() / 60 + context.getResources().getString(R.string.timeHour) + " " + event.getTimeLeft() % 24 + context.getResources().getString(R.string.timeMinute) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
        else if(event.getTimeLeft() >= 0){ textViewLeftTime.setText("( 0" + context.getResources().getString(R.string.timeHour) + " " + event.getTimeLeft() + context.getResources().getString(R.string.timeMinute) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
        else { textViewLeftTime.setText("too late :("); }


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm"); // 24 hours
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US); // 12 hours with am/pm
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
        Date date = new Date();
        date.setTime(event.getEventDate() * 1000);
        textViewTime.setText(dateFormat.format(date));

        ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();

        NetworkImageView header = (NetworkImageView) findViewById(R.id.header);
        header.setImageUrl("http://www.berkugudur.com/narr3/images/" + event.getId() + "_" + event.getEventVersion() + "_0.jpg", imageLoader);

        for(int i = 0; i < event.getImageCount(); i++) {new NetworkImageView(context);
            nivList.get(i).setVisibility(View.VISIBLE);
            nivList.get(i).setImageUrl("http://www.berkugudur.com/narr3/images/" + event.getId() + "_" + event.getEventVersion() + "_" + i + "_s.jpg", imageLoader);
            final int currentImage = i;
            nivList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("eventId", event.getId());
                    intent.putExtra("imageCount", event.getImageCount());
                    intent.putExtra("eventVersion", event.getEventVersion());
                    intent.putExtra("currentImage", currentImage);
                    context.startActivity(intent);
                }
            });
        }

        if(event.getImageCount() == 0) { scrollViewImages.setVisibility(View.GONE); }

        getExtras();

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

    private void getExtras() {
        try {
            extrasTask();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void extrasTask() throws JSONException {
        JSONObject postObj = new JSONObject();

        postObj.put("event_id", event.getId());

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, "http://www.berkugudur.com/narr3/query.php?action=eventInformation", postObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                try {
                    textViewName.setText(result.getString("event_creator_name"));
                    name = result.getString("event_creator_name");
                    textViewInformation.setText(result.getString("event_information"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(layout, context.getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

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
}
