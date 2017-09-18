package br.cesupa.fisiovr.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.adapter.SimpleFisioterapeutaRecyclerViewAdapter;
import br.cesupa.fisiovr.adapter.SimpleVideoRecyclerViewAdapter;
import br.cesupa.fisiovr.dummy.DummyContent;
import br.cesupa.fisiovr.dummy.VideoContent;
import br.cesupa.fisiovr.home;
import br.cesupa.fisiovr.util.Util;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class VideoListActivity extends AppCompatActivity {

    public static final String TAG_VIDEOS_CACHE = "tag_videos_cache";

    public static final String TAG_VIDEOS_TYPE = "tag_videos_type";

    public static final int SAVED_VIDEOS_TYPE = 0;

    public static final int NEW_VIDEOS_TYPE = 1;

    RecyclerView video_recycleview;

    Cache cache;

    SharedPreferences settingsVideoListActivity;

    SwipeRefreshLayout swipe_video_list_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        settingsVideoListActivity = getSharedPreferences(getClass().getName(), 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_video_list_activity);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String titleActionBar = "Vídeos";
            if(getIntent().hasExtra(TAG_VIDEOS_TYPE)){
                switch (getIntent().getIntExtra(TAG_VIDEOS_TYPE, -1)){
                    case NEW_VIDEOS_TYPE: titleActionBar = "Novos Vídeos"; break;
                    case SAVED_VIDEOS_TYPE: titleActionBar = "Vídeos Salvos"; break;
                }
            }

            actionBar.setTitle(titleActionBar);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        swipe_video_list_activity = (SwipeRefreshLayout) findViewById(R.id.swipe_video_list_activity);
        swipe_video_list_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Util.isConnect(VideoListActivity.this)) {
                    getVideos();
                }
            }
        });

        video_recycleview = (RecyclerView) findViewById(R.id.video_recycleview);
        video_recycleview.setAdapter(new SimpleVideoRecyclerViewAdapter());
        if(Util.isConnect(this)) {
            if(settingsVideoListActivity.contains(TAG_VIDEOS_CACHE)){
                populateRecycleView();
            }else{
                getVideos();
            }
        }
    }

    private void getVideos(){
        cache = new DiskBasedCache(getCacheDir());
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);

        mRequestQueue.start();
        mRequestQueue.add(new JsonArrayRequest(Request.Method.GET, "https://jsonplaceholder.typicode.com/comments", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                SharedPreferences.Editor editorSettingsVideoListActivity = settingsVideoListActivity.edit();
                editorSettingsVideoListActivity.putString(TAG_VIDEOS_CACHE, response.toString());
                editorSettingsVideoListActivity.apply();

                populateRecycleView();
                cache.clear();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VideoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                cache.clear();
                error.printStackTrace();
            }
        }));
    }

    private void populateRecycleView(){
        try {
            JSONArray response = new JSONArray(settingsVideoListActivity.getString(TAG_VIDEOS_CACHE, null));
            SimpleVideoRecyclerViewAdapter adapter = new SimpleVideoRecyclerViewAdapter();
            for(int i = 0; i < response.length(); i++){
                JSONObject repo = response.getJSONObject(i);
                adapter.addItem(new VideoContent.VideoItem(
                        repo.getString("id"),
                        repo.getString("name"),
                        repo.getString("email") + "\n" + repo.getString("body")
                ));
            }

            video_recycleview.setAdapter(adapter);
            if(swipe_video_list_activity.isRefreshing())
                swipe_video_list_activity.setRefreshing(false);

        } catch (JSONException e) {
            Log.e(getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
