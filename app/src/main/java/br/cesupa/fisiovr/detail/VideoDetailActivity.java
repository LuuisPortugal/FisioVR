package br.cesupa.fisiovr.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.dummy.DummyContent;
import br.cesupa.fisiovr.dummy.VideoContent;
import br.cesupa.fisiovr.home;
import br.cesupa.fisiovr.list.VideoListActivity;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class VideoDetailActivity extends AppCompatActivity {

    public static final String ARG_VIDEO_ID = "video_id";

    private VideoContent.VideoItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar_video_detail_activity);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_video_detail_activity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras().containsKey(ARG_VIDEO_ID)) {
            Gson gson = new Gson();
            mItem = gson.fromJson(getIntent().getExtras().getString(ARG_VIDEO_ID), VideoContent.VideoItem.class);

            if(mItem != null) {
                actionBar.setTitle(mItem.content);
                ((TextView) findViewById(R.id.text_view_video_detail_activity)).setText(mItem.details);
            }
        }
    }
}
