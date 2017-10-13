package br.cesupa.fisiovr.list;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.adapter.SimplePacienteRecyclerViewAdapter;
import br.cesupa.fisiovr.home;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class PacienteListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_paciente_list_activity);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Pacientes");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView paciente_recycleview = (RecyclerView) findViewById(R.id.paciente_recycleview);
        paciente_recycleview.setAdapter(new SimplePacienteRecyclerViewAdapter());
    }
}
