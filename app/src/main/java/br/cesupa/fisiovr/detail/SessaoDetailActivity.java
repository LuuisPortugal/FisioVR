package br.cesupa.fisiovr.detail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.dummy.SessaoContent;
import br.cesupa.fisiovr.home;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class SessaoDetailActivity extends AppCompatActivity {

    public static final String ARG_SESSAO_ID = "sessao_id";

    private SessaoContent.SessaoItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessao_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar_sessao_detail_activity);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_sessao_detail_activity);
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

        if (getIntent().getExtras().containsKey(ARG_SESSAO_ID)) {
            Gson gson = new Gson();
            mItem = gson.fromJson(getIntent().getExtras().getString(ARG_SESSAO_ID), SessaoContent.SessaoItem.class);

            if (mItem != null) {
                actionBar.setTitle(mItem.content);
                ((TextView) findViewById(R.id.text_view_sessao_detail_activity)).setText(mItem.details);
            }
        }
    }
}
