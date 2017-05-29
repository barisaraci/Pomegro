package com.pomegro.android.newsfeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import com.pomegro.android.event.Event;
import com.pomegro.android.event.EventAdapter;
import com.pomegro.android.utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class NewsfeedFragment extends Fragment {

    private Context context;
    private SharedPreferences preferences;
    private ArrayList<Event> eventList;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeLayout;

    private boolean isLoading = true;
    private boolean noMoreQuestion = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewEvents);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setScrollBarStyle(RecyclerView.SCROLLBARS_OUTSIDE_OVERLAY);

        eventList = new ArrayList<>();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && !noMoreQuestion && totalItemCount >= 10) { // ilk açılışta gelen etkinlik sayısı
                        isLoading = true;
                        try {
                            refreshTask(2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        swipeLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    refreshTask(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            refreshTask(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_newsfeed, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                final ArrayList<Event> filteredModelList = filter(eventList, query);
                eventAdapter.animateTo(filteredModelList);
                recyclerView.scrollToPosition(0);
                return true;
            }
        });
    }

    private ArrayList<Event> filter(ArrayList<Event> models, String query) {
        query = query.toLowerCase();
        final ArrayList<Event> filteredModelList = new ArrayList<>();
        for (Event model : models) {
            final String text = model.getTopic().toLowerCase() + model.getName().toLowerCase() + model.getInterests().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void refreshTask(final int refType) throws JSONException {

        String userID = preferences.getString("userid", null);
        String refreshDate = "0";

        if(refType == 0) {
            refreshDate = "0";
        } else if(refType == 1) {
            refreshDate = preferences.getString("refreshdate", null);
        } else if(refType == 2) {
            refreshDate = preferences.getString("lasteventdate", null);
        }

        JSONObject postObj = new JSONObject();

        postObj.put("userID", userID);
        postObj.put("refreshDate", refreshDate);
        postObj.put("refreshType", refType);

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, "http://www.berkugudur.com/narr3/query.php?action=newsfeed", postObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                SharedPreferences.Editor editor = preferences.edit();

                try {
                    if(!result.getJSONObject("generalInfo").getBoolean("newEvent")) {
                        Snackbar.make(getView(), context.getResources().getString(R.string.noNewEvent), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        if(refType == 2) { noMoreQuestion = true; }
                    } else {
                        String refreshDate = result.getJSONObject("generalInfo").getString("refreshDate");
                        String lastEventDate = result.getJSONObject("generalInfo").getString("lastEventDate");

                        long refreshDateNumber = result.getJSONObject("generalInfo").getLong("refreshDateNumber");

                        if(refType == 0) {
                            editor.putString("refreshdate", refreshDate);
                            editor.putString("lasteventdate", lastEventDate);
                            editor.apply();
                        } else if(refType == 1) {
                            editor.putString("refreshdate", refreshDate);
                            editor.apply();
                        } else if(refType == 2) {
                            editor.putString("lasteventdate", lastEventDate);
                            editor.apply();
                        }

                        for(int i = 1; i <= result.getJSONObject("generalInfo").getInt("eventNumber"); i++) {
                            Event event = new Event();
                            event.setId(result.getJSONObject("event" + Integer.toString(i)).getInt("event_id"));
                            event.setType(result.getJSONObject("event" + Integer.toString(i)).getInt("user_action"));
                            event.setName(result.getJSONObject("event" + Integer.toString(i)).getString("action_username"));
                            event.setTopic(result.getJSONObject("event" + Integer.toString(i)).getString("event_name"));
                            event.setImageCount(result.getJSONObject("event" + Integer.toString(i)).getInt("event_image_count"));
                            event.setLocation(result.getJSONObject("event" + Integer.toString(i)).getString("event_location"));
                            event.setContent(result.getJSONObject("event" + Integer.toString(i)).getString("event_content"));
                            event.setMaxAtt(result.getJSONObject("event" + Integer.toString(i)).getInt("event_max_att"));
                            event.setCurAtt(result.getJSONObject("event" + Integer.toString(i)).getInt("event_cur_att"));
                            event.setEventDate(result.getJSONObject("event" + Integer.toString(i)).getLong("event_start_date"));
                            event.setActionTime(refreshDateNumber - result.getJSONObject("event" + Integer.toString(i)).getLong("user_action_time"));
                            event.setInterests(result.getJSONObject("event" + Integer.toString(i)).getString("event_interests"));
                            event.setEventVersion(result.getJSONObject("event" + Integer.toString(i)).getInt("event_image_version"));
                            event.setTimeLeft(result.getJSONObject("event" + Integer.toString(i)).getLong("event_start_date") - refreshDateNumber);
                            event.setActionTime(event.getActionTime() / 60);
                            event.setTimeLeft(event.getTimeLeft() / 60);

                            if(refType == 0) {
                                eventList.add(event);
                            }else if(refType == 1) {
                                eventAdapter.addItem(0, event);
                                layoutManager.scrollToPositionWithOffset(0, 0);
                            }else if(refType == 2) {
                                eventAdapter.addItem(eventAdapter.getItemCount() - 1, event);
                            }
                        }

                        if(refType==0){
                            eventAdapter = new EventAdapter(context, eventList);
                            recyclerView.setAdapter((new SlideInLeftAnimationAdapter(eventAdapter)));
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                isLoading = false;
                swipeLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeLayout.setRefreshing(false);
                Snackbar.make(getView(), context.getResources().getString(R.string.siresult3), Snackbar.LENGTH_LONG)
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

        VolleySingleton.getInstance(context).addToRequestQueue(sr);
    }

}
