package br.cesupa.fisiovr.list;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.adapter.SimpleFisioterapeutaRecyclerViewAdapter;
import br.cesupa.fisiovr.home;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class FisioterapeutaListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisioterapeuta_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fisioterapeuta_list_activity);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Fisioterapeutas");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView fisioterapeuta_recycleview = (RecyclerView) findViewById(R.id.fisioterapeuta_recycleview);
        fisioterapeuta_recycleview.setAdapter(new SimpleFisioterapeutaRecyclerViewAdapter());
    }
}
