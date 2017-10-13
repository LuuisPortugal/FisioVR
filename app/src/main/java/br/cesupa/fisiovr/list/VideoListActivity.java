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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
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
public class VideoListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ValueEventListener {

    SearchView searchView;
    FirebaseDatabase database;
    RecyclerView video_recycleview;
    List<VideoContent.VideoItem> videos;
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

        database = FirebaseDatabase.getInstance();
        database.getReference("videos").addListenerForSingleValueEvent(this);

        video_recycleview = (RecyclerView) findViewById(R.id.video_recycleview);
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
    public void onDataChange(DataSnapshot dataSnapshot) {
        videos = Linq.stream(
                dataSnapshot
                        .getValue(new GenericTypeIndicator<HashMap<String, VideoContent.VideoItem>>() {
                        })
                        .values()
        )
                .where(new Predicate<VideoContent.VideoItem>() {
                    @Override
                    public boolean apply(VideoContent.VideoItem videoItem) {
                        return videoItem.thumbnail != null && !videoItem.thumbnail.isEmpty();
                    }
                })
                .orderBy(new Selector<VideoContent.VideoItem, String>() {
                    @Override
                    public String select(VideoContent.VideoItem videoItem) {
                        return videoItem.title;
                    }
                })
                .toList();

        String querySearch = searchView != null ? searchView.getQuery().toString() : "";
        updateAdapterByQuery(querySearch);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Toast.makeText(VideoListActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        if (database != null) {
            database.goOffline();
        }

        super.onDestroy();
    }
}
