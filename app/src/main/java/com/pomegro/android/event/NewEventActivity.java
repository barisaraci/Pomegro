package com.pomegro.android.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pomegro.android.R;
import com.pomegro.android.utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NewEventActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences preferences;

    private View layout;
    private LinearLayout layoutImages;
    private ImageView imageNewImage;
    private EditText topicField, contentField, dataField, locationField, maxAttField;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner spinner1, spinner2, spinner3;
    private Button buttonCreate;

    private ArrayList<String> images = new ArrayList<String>();
    private int PICK_IMAGE_REQUEST = 1;
    private int imageNumber = 0;
    private boolean canImageAdd = true, noResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        context = this;
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        layout = findViewById(R.id.mainLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutImages = (LinearLayout) findViewById(R.id.linearLayoutImages);

        imageNewImage = new ImageView(context);
        imageNewImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.addimage));
        imageNewImage.setLayoutParams(new LinearLayout.LayoutParams((int) (150 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (150 * context.getResources().getDisplayMetrics().density + 0.5f)));
        imageNewImage.setPadding((int) (4 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (6 * context.getResources().getDisplayMetrics().density + 0.5f),
                (int) (4 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (6 * context.getResources().getDisplayMetrics().density + 0.5f));
        imageNewImage.setTag("pointerImage");
        layoutImages.addView(imageNewImage);

        imageNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        topicField = (EditText )findViewById(R.id.editTextTopic);
        contentField = (EditText) findViewById(R.id.editTextContent);
        dataField = (EditText) findViewById(R.id.editTextData);
        locationField = (EditText) findViewById(R.id.editTextLocation);
        maxAttField = (EditText) findViewById(R.id.editTextParticipantNumber);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        spinner1 = (Spinner) findViewById(R.id.spinnerInterest1);
        spinner2 = (Spinner) findViewById(R.id.spinnerInterest2);
        spinner3 = (Spinner) findViewById(R.id.spinnerInterest3);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.interests_array, R.layout.item_spinner);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.interests_array, R.layout.item_spinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.interests_array, R.layout.item_spinner);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);
        spinner2.setVisibility(View.GONE);
        spinner3.setVisibility(View.GONE);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinner1.getSelectedItemPosition() != 0) {
                    spinner2.setVisibility(View.VISIBLE);
                } else {
                    spinner2.setVisibility(View.GONE);
                    spinner3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinner2.getSelectedItemPosition() != 0) {
                    spinner3.setVisibility(View.VISIBLE);
                } else {
                    spinner3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_image_details, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void createEvent() {
        if(topicField.getText().length() != 0 && contentField.getText().length() != 0 && dataField.getText().length() != 0 && locationField.getText().length() != 0 && maxAttField.getText().length() != 0
                && spinner1.getSelectedItemPosition() != 0) {
            buttonCreate.setVisibility(View.GONE);
            try {
                createTask();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Snackbar.make(layout, context.getResources().getString(R.string.lackingData), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    private void createTask() throws JSONException {
        Snackbar.make(layout, context.getResources().getString(R.string.eventCreating), Snackbar.LENGTH_LONG).setAction("Action", null).show();

        JSONObject postObj = new JSONObject();

        postObj.put("event_creator_id", preferences.getString("userid", null));
        postObj.put("event_name", topicField.getText().toString());
        postObj.put("event_content", contentField.getText().toString());
        postObj.put("event_information", dataField.getText().toString());
        postObj.put("event_location", locationField.getText().toString());
        postObj.put("event_max_att", maxAttField.getText().toString());

        String date, month;
        if(datePicker.getMonth() + 1 < 10) {
            month = "0" + (datePicker.getMonth() + 1);
        } else {
            month = "" + datePicker.getMonth() + 1;
        }
        if(Build.VERSION.SDK_INT >= 23 ) {
            date = datePicker.getYear() + "-" + month + "-" + datePicker.getDayOfMonth() + " " + timePicker.getHour() + ":" + timePicker.getMinute() + ":00";
        } else {
            date = datePicker.getYear() + "-" + month + "-" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + ":00";
        }

        postObj.put("event_start_date", date);
        postObj.put("event_int1", spinner1.getSelectedItemPosition());
        postObj.put("event_int2", spinner2.getSelectedItemPosition());
        postObj.put("event_int3", spinner3.getSelectedItemPosition());

        JSONArray imageArray = new JSONArray();

        for (int i = 0; i < layoutImages.getChildCount(); i++){
            View view = layoutImages.getChildAt(i);

            if(view.getTag() != "pointerImage") {
                imageArray.put(view.getTag());
            }
        }

        postObj.put("event_images", imageArray);

        noResult = false;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!noResult) {
                    Snackbar.make(layout, context.getResources().getString(R.string.eventLongProgress), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        }, 20000);

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, "http://www.berkugudur.com/narr3/query.php?action=addEvent", postObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                int answer = 0;
                try {
                    answer = result.getInt("answer");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                buttonCreate.setVisibility(View.VISIBLE);
                noResult = true;
                if(answer == 1) {
                    Snackbar.make(layout, context.getResources().getString(R.string.eventCreated), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Activity) context).finish();
                        }
                    }, 1500);
                } else {
                    Snackbar.make(layout, context.getResources().getString(R.string.eventNotCreated), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(layout, context.getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                buttonCreate.setVisibility(View.VISIBLE);
                noResult = true;

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

        jr.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(jr);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                final RelativeLayout layout = new RelativeLayout(context);
                final ImageView imageViewCross = new ImageView(context);
                Bitmap bitmap = getCorrectlyOrientedImage(this, uri);

                Bitmap newBitmap = bitmap;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                layout.setTag(encodedImage);
                baos.close();

                final ImageView imageView = new ImageView(context);
                imageViewCross.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_close_clear_cancel));
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins((int) (5 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (5 * context.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
                imageViewCross.setLayoutParams(lp);
                imageViewCross.setTag(imageNumber);
                imageViewCross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewGroup) layout.getParent()).removeView(layout);
                        imageNumber--;
                        images.remove(imageViewCross.getTag());
                        if (imageNumber < 6 && !canImageAdd) {
                            ImageView imageNewImagee = new ImageView(context);
                            imageNewImagee.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.addimage));
                            imageNewImagee.setLayoutParams(new LinearLayout.LayoutParams((int) (150 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (150 * context.getResources().getDisplayMetrics().density + 0.5f)));
                            imageNewImagee.setPadding((int) (4 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (6 * context.getResources().getDisplayMetrics().density + 0.5f),
                                    (int) (4 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (6 * context.getResources().getDisplayMetrics().density + 0.5f));
                            imageNewImagee.setTag("pointerImage");

                            imageNewImagee.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                                }
                            });

                            imageNewImage = imageNewImagee;
                            layoutImages.addView(imageNewImage);
                            canImageAdd = true;
                        }
                    }
                });

                Bitmap srcBmp = bitmap;
                Bitmap dstBmp;
                if (srcBmp.getWidth() >= srcBmp.getHeight()) {
                    dstBmp = Bitmap.createBitmap(
                            srcBmp,
                            srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                            0,
                            srcBmp.getHeight(),
                            srcBmp.getHeight()
                    );
                } else {
                    dstBmp = Bitmap.createBitmap(
                            srcBmp,
                            0,
                            srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                            srcBmp.getWidth(),
                            srcBmp.getWidth()
                    );
                }

                imageView.setImageBitmap(dstBmp);
                imageView.setLayoutParams(new LinearLayout.LayoutParams((int) (150 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (150 * context.getResources().getDisplayMetrics().density + 0.5f)));
                imageView.setPadding((int) (4 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (6 * context.getResources().getDisplayMetrics().density + 0.5f),
                        (int) (4 * context.getResources().getDisplayMetrics().density + 0.5f), (int) (6 * context.getResources().getDisplayMetrics().density + 0.5f));
                layout.addView(imageView);
                layout.addView(imageViewCross);
                layoutImages.addView(layout, 0);
                imageNumber++;
                if(imageNumber >= 6 && canImageAdd) {
                    ((ViewGroup) imageNewImage.getParent()).removeView(imageNewImage);
                    canImageAdd = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 540 || rotatedHeight > 960) {
            float widthRatio = ((float) rotatedWidth) / ((float) 540);
            float heightRatio = ((float) rotatedHeight) / ((float) 960);
            float maxRatio = Math.max(widthRatio, heightRatio);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

}
