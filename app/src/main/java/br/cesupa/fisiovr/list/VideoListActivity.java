package br.cesupa.fisiovr.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.adapter.SimpleFisioterapeutaRecyclerViewAdapter;
import br.cesupa.fisiovr.adapter.SimpleVideoRecyclerViewAdapter;
import br.cesupa.fisiovr.dummy.DummyContent;
import br.cesupa.fisiovr.dummy.VideoContent;
import br.cesupa.fisiovr.home;
import br.cesupa.fisiovr.util.Util;
import br.com.zbra.androidlinq.Linq;
import br.com.zbra.androidlinq.delegate.Predicate;
import br.com.zbra.androidlinq.delegate.Selector;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class VideoListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ValueEventListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG_VIDEOS_CACHE = "tag_videos_cache";

    public static final String TAG_VIDEOS_TYPE = "tag_videos_type";

    public static final int SAVED_VIDEOS_TYPE = 0;

    public static final int NEW_VIDEOS_TYPE = 1;

    RecyclerView video_recycleview;

    SwipeRefreshLayout swipe_video_list_activity;

    FirebaseDatabase database;

    DatabaseReference tableDatabase;

    List<VideoContent.VideoItem> videos;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_video_list_activity);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Vídeos");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        swipe_video_list_activity = (SwipeRefreshLayout) findViewById(R.id.swipe_video_list_activity);
        swipe_video_list_activity.setOnRefreshListener(this);

        database = FirebaseDatabase.getInstance();
        tableDatabase = database.getReference("videos");

        video_recycleview = (RecyclerView) findViewById(R.id.video_recycleview);
        getVideos();
    }

    private void getVideos() {
        swipe_video_list_activity.setRefreshing(true);
        tableDatabase.addValueEventListener(this);
    }

    private void updateAdapterByQuery(final String query) {
        if (TextUtils.isEmpty(query)) {
            SimpleVideoRecyclerViewAdapter adapter = new SimpleVideoRecyclerViewAdapter(videos);
            video_recycleview.setAdapter(adapter);
        } else {
            List<VideoContent.VideoItem> videosForQuery = videos;
            SimpleVideoRecyclerViewAdapter adapter = new SimpleVideoRecyclerViewAdapter(
                Linq.stream(videosForQuery)
                    .where(new Predicate<VideoContent.VideoItem>() {
                        @Override
                        public boolean apply(VideoContent.VideoItem videoItem) {
                            return videoItem.title.toLowerCase().contains(query.toLowerCase());
                        }
                    })
                    .orderBy(new Selector<VideoContent.VideoItem, String>() {
                        @Override
                        public String select(VideoContent.VideoItem videoItem) {
                            return videoItem.title;
                        }
                    })
                    .toList()
            );
            video_recycleview.setAdapter(adapter);
        }
    }

    @Override
    public void onRefresh() {
        getVideos();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        videos = Linq.stream(
            dataSnapshot
                .getValue(new GenericTypeIndicator<HashMap<String, VideoContent.VideoItem>>() {})
                .values()
        )
        .orderBy(new Selector<VideoContent.VideoItem, String>() {
            @Override
            public String select(VideoContent.VideoItem videoItem) {
                return videoItem.title;
            }
        }).toList();

        String querySearch = searchView != null ? searchView.getQuery().toString() : "";
        updateAdapterByQuery(querySearch);
        swipe_video_list_activity.setRefreshing(false);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Toast.makeText(VideoListActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
        swipe_video_list_activity.setRefreshing(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        updateAdapterByQuery(newText);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.videos, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search_videos).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onDestroy(){
        if(database != null){
            database.goOffline();
        }

        super.onDestroy();
    }
}
