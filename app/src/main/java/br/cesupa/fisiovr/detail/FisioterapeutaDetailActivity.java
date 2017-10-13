package br.cesupa.fisiovr.detail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.dummy.FisioterapeutaContent;
import br.cesupa.fisiovr.home;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class FisioterapeutaDetailActivity extends AppCompatActivity {

    public static final String ARG_FISIOTERAPEUTA_ID = "Fisioterapeuta_id";

    private FisioterapeutaContent.FisioterapeutaItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisioterapeuta_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar_fisioterapeuta_detail_activity);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_fisioterapeuta_detail_activity);
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

        if (getIntent().getExtras().containsKey(ARG_FISIOTERAPEUTA_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = FisioterapeutaContent.ITEM_MAP.get(getIntent().getExtras().getString(ARG_FISIOTERAPEUTA_ID));
            actionBar.setTitle(mItem.content);
        }

        if (mItem != null) {
            ((TextView) findViewById(R.id.item_detail)).setText(mItem.details);
        }
    }
}
