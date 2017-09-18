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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.adapter.SimpleSessaoRecyclerViewAdapter;
import br.cesupa.fisiovr.dummy.SessaoContent;
import br.cesupa.fisiovr.home;
import br.cesupa.fisiovr.util.Util;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class SessaoListActivity extends AppCompatActivity {

    private static final String TAG_SESSAO_CACHE = "tag_sessao_cache";

    RecyclerView sessao_recycleview;

    Cache cache;

    SharedPreferences settingsSessaoListActivity;

    SwipeRefreshLayout swipe_sessao_list_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessao_list);

        settingsSessaoListActivity = getSharedPreferences(getClass().getName(), 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sessao_list_activity);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Sessões");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        swipe_sessao_list_activity = (SwipeRefreshLayout) findViewById(R.id.swipe_sessao_list_activity);
        swipe_sessao_list_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Util.isConnect(SessaoListActivity.this)) {
                    getSessoes();
                }
            }
        });

        sessao_recycleview = (RecyclerView) findViewById(R.id.sessao_recycleview);
        sessao_recycleview.setAdapter(new SimpleSessaoRecyclerViewAdapter());
        if(Util.isConnect(this)) {
            if(settingsSessaoListActivity.contains(TAG_SESSAO_CACHE)){
                populateRecycleView();
            }else{
                getSessoes();
            }
        }
    }

    private void getSessoes(){
        cache = new DiskBasedCache(getCacheDir());
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);

        mRequestQueue.start();
        mRequestQueue.add(new JsonArrayRequest(Request.Method.GET, "https://jsonplaceholder.typicode.com/users", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                SharedPreferences.Editor editorSettingsSessaoListActivity = settingsSessaoListActivity.edit();
                editorSettingsSessaoListActivity.putString(TAG_SESSAO_CACHE, response.toString());
                editorSettingsSessaoListActivity.apply();

                populateRecycleView();
                cache.clear();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SessaoListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                cache.clear();
                error.printStackTrace();
            }
        }));
    }

    private void populateRecycleView(){
        try {
            JSONArray response = new JSONArray(settingsSessaoListActivity.getString(TAG_SESSAO_CACHE, null));
            SimpleSessaoRecyclerViewAdapter adapter = new SimpleSessaoRecyclerViewAdapter();
            for(int i = 0; i < response.length(); i++){
                JSONObject repo = response.getJSONObject(i);

                StringBuilder detail = new StringBuilder();
                detail.append(repo.getString("email")); detail.append("\n");
                detail.append(repo.getString("phone")); detail.append("\n");

                detail.append(repo.getJSONObject("address").getString("street"));
                detail.append(", ");
                detail.append(repo.getJSONObject("address").getString("suite"));
                detail.append(", ");
                detail.append(repo.getJSONObject("address").getString("city"));
                detail.append(", ");
                detail.append(repo.getJSONObject("address").getString("zipcode"));
                detail.append("\n");

                detail.append(repo.getString("website")); detail.append("\n");

                detail.append(repo.getJSONObject("company").getString("name"));
                detail.append(", ");
                detail.append(repo.getJSONObject("company").getString("catchPhrase"));

                adapter.addItem(new SessaoContent.SessaoItem(
                        repo.getString("id"),
                        repo.getString("name") + " " + repo.getString("username"),
                        detail.toString()
                ));
            }

            sessao_recycleview.setAdapter(adapter);
            if(swipe_sessao_list_activity.isRefreshing())
                swipe_sessao_list_activity.setRefreshing(false);

        } catch (JSONException e) {
            Log.e(getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
