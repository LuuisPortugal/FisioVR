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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.adapter.SimpleVideoRecyclerViewAdapter;
import br.cesupa.fisiovr.dummy.PlaylistContent;
import br.cesupa.fisiovr.dummy.VideoContent;
import br.com.zbra.androidlinq.Linq;
import br.com.zbra.androidlinq.delegate.Predicate;
import br.com.zbra.androidlinq.delegate.Selector;

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
        refreshRecycleView();
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

    public void refreshRecycleView() {
        Volley.newRequestQueue(this).add(
                new StringRequest(Request.Method.GET, "https://www.googleapis.com/youtube/v3/search?key=AIzaSyBwHq8UHDRTAFtGTt6aTYNyCpZXPx-GH3U&channelId=UCzuqhhs6NWbgTzMuM09WKDQ&part=snippet",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseChannelString) {
                                try {
                                    PlaylistContent.Playlist responseChannel = new Gson().fromJson(responseChannelString, PlaylistContent.Playlist.class);
                                    for (PlaylistContent.Playlist playlist :
                                            Linq
                                                    .stream(responseChannel.items)
                                                    .where(new Predicate<PlaylistContent.Playlist>() {
                                                        @Override
                                                        public boolean apply(PlaylistContent.Playlist playlist) {
                                                            return playlist.id.get("kind").equals("youtube#playlist");
                                                        }
                                                    })
                                                    .toList()
                                            ) {
                                        getItemPlaylist(playlist.id.get("playlistId"));
                            }
                                } catch (Exception error) {
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

    public void getItemPlaylist(String playlistID) {
        System.out.println(playlistID);
        Volley.newRequestQueue(this).add(
                new StringRequest(Request.Method.GET,
                        "https://www.googleapis.com/youtube/v3/playlistItems?key=AIzaSyBwHq8UHDRTAFtGTt6aTYNyCpZXPx-GH3U&part=snippet&playlistId=" + playlistID,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responsePlaylistString) {
                                try {
                                    VideoContent.VideoItem responsePlaylist = new Gson().fromJson(responsePlaylistString, VideoContent.VideoItem.class);
                                    for (VideoContent.VideoItem videoItem :
                                            Linq
                                                    .stream(responsePlaylist.items)
                                                    .where(new Predicate<VideoContent.VideoItem>() {
                                                        @Override
                                                        public boolean apply(VideoContent.VideoItem videoItem) {
                                                            return videoItem.snippet.resourceId.get("kind").equals("youtube#video");
                                                        }
                                                    })
                                                    .toList()
                                            ) {
                                        getVideoItemPlaylist(videoItem.snippet.resourceId.get("videoId"));
                            }
                                } catch (Exception error) {
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

    private void getVideoItemPlaylist(String videoID) {
        System.out.println(videoID);
        Volley.newRequestQueue(this).add(
                new StringRequest(Request.Method.GET,
                        "https://www.googleapis.com/youtube/v3/videos?key=AIzaSyBwHq8UHDRTAFtGTt6aTYNyCpZXPx-GH3U&part=snippet,statistics&id=" + videoID,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseVideoString) {
                                try {
                                    VideoContent.VideoItem responseVideo = new Gson().fromJson(responseVideoString, VideoContent.VideoItem.class);
                                    for (VideoContent.VideoItem videoItem :
                                            Linq
                                                    .stream(responseVideo.items)
                                                    .where(new Predicate<VideoContent.VideoItem>() {
                                                        @Override
                                                        public boolean apply(VideoContent.VideoItem videoItem) {
                                                            return videoItem.kind.equals("youtube#video");
                                                        }
                                                    })
                                                    .toList()
                                            ) {
                                        videos.add(videoItem);
                            }

                                    String querySearch = searchView != null ? searchView.getQuery().toString() : "";
                                    updateAdapterByQuery(querySearch);
                                } catch (Exception error) {
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
                                    return videoItem.snippet.title.toLowerCase().contains(query.toLowerCase());
                                }
                            })
                            .orderBy(new Selector<VideoContent.VideoItem, String>() {
                                @Override
                                public String select(VideoContent.VideoItem videoItem) {
                                    return videoItem.snippet.title;
                                }
                            })
                            .toList()
            );
            video_recycleview.setAdapter(adapter);
        }

        progressDialog.dismiss();
    }
}
