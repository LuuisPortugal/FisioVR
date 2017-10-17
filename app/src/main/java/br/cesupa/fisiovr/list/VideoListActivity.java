package br.cesupa.fisiovr.list;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.adapter.SimpleVideoRecyclerViewAdapter;
import br.cesupa.fisiovr.dummy.VideoContent;
import br.cesupa.fisiovr.home;
import br.com.zbra.androidlinq.Linq;
import br.com.zbra.androidlinq.delegate.Predicate;
import br.com.zbra.androidlinq.delegate.Selector;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class VideoListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;
    RecyclerView video_recycleview;
    List<VideoContent.VideoItem> videos = new ArrayList<>();
    ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(VideoListActivity.this);
        progressDialog.setMessage("Carregando");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        video_recycleview = (RecyclerView) findViewById(R.id.video_recycleview);

        Volley.newRequestQueue(this).add(
                new JsonObjectRequest(Request.Method.GET, "https://www.googleapis.com/youtube/v3/search?key=AIzaSyBwHq8UHDRTAFtGTt6aTYNyCpZXPx-GH3U&channelId=UCzuqhhs6NWbgTzMuM09WKDQ&part=snippet", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseChannel) {
                                try {
                                    JSONArray itens = responseChannel.getJSONArray("items");
                                    for (int i = 0; i < itens.length(); i++) {
                                        JSONObject playlist = itens.getJSONObject(i);
                                        JSONObject id = playlist.getJSONObject("id");

                                        if (id.getString("kind").equals("youtube#playlist"))
                                            getItemPlaylist(id.getString("playlistId"));
                            }

                                    String querySearch = searchView != null ? searchView.getQuery().toString() : "";
                                    updateAdapterByQuery(querySearch);
                                } catch (JSONException error) {
                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(VideoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(VideoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                )
        );

        String querySearch = searchView != null ? searchView.getQuery().toString() : "";
        updateAdapterByQuery(querySearch);
    }

    public void getItemPlaylist(String playlistID) {
        Volley.newRequestQueue(this).add(
                new JsonObjectRequest(Request.Method.GET,
                        "https://www.googleapis.com/youtube/v3/playlistItems?key=AIzaSyBwHq8UHDRTAFtGTt6aTYNyCpZXPx-GH3U&part=snippet&playlistId=" + playlistID, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responsePLaylist) {
                                try {
                                    JSONArray itemsPlaylist = responsePLaylist.getJSONArray("items");
                                    for (int i = 0; i < itemsPlaylist.length(); i++) {
                                        JSONObject item = itemsPlaylist.getJSONObject(i);
                                        JSONObject snippet = item.getJSONObject("snippet");
                                        JSONObject resourceId = snippet.getJSONObject("resourceId");

                                        if (resourceId.getString("kind").equals("youtube#video"))
                                            getVideoPlaylist(resourceId.getString("videoId"));
                                    }
                                } catch (JSONException error) {
                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(VideoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(VideoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                )
        );
    }

    private void getVideoPlaylist(String videoID) {
        Volley.newRequestQueue(this).add(
                new JsonObjectRequest(Request.Method.GET,
                        "https://www.googleapis.com/youtube/v3/videos?key=AIzaSyBwHq8UHDRTAFtGTt6aTYNyCpZXPx-GH3U&part=snippet,statistics,status&id=" + videoID, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseVideo) {
                                try {
                                    JSONArray itemsPlaylist = responseVideo.getJSONArray("items");
                                    for (int i = 0; i < itemsPlaylist.length(); i++) {
                                        JSONObject item = itemsPlaylist.getJSONObject(i);
                                        JSONObject statistics = item.getJSONObject("statistics");
                                        JSONObject snippet = item.getJSONObject("snippet");
                                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                        String tags = snippet.getString("tags");

                                        assert videos != null;
                                        videos.add(
                                                new VideoContent.VideoItem(
                                                        item.getString("id"),
                                                        snippet.getString("title"),
                                                        thumbnails.getJSONObject("default").getString("url"),
                                                        snippet.getString("publishedAt"),
                                                        statistics.getString("viewCount"),
                                                        new ArrayList<String>(),
                                                        new ArrayList<String>(),
                                                        snippet.getString("description"),
                                                        "",
                                                        ""
                                                )
                                        );
                                    }
                                } catch (JSONException error) {
                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(VideoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(VideoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                )
        );
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

        progressDialog.dismiss();
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
}
