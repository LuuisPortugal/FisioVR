package br.cesupa.fisiovr.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.detail.SessaoDetailActivity;
import br.cesupa.fisiovr.dummy.SessaoContent;

/**
 * Created by luis.portugal on 01/08/2017.
 */

public class SimpleSessaoRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleSessaoRecyclerViewAdapter.ViewHolder> {

    private List<SessaoContent.SessaoItem> mValues = new ArrayList<SessaoContent.SessaoItem>();

    public SimpleSessaoRecyclerViewAdapter() {
    }

    public void addItem(SessaoContent.SessaoItem item) {
        mValues.add(item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View itemClicked) {
                Intent intent = new Intent(itemClicked.getContext(), SessaoDetailActivity.class);
                intent.putExtra(
                        SessaoDetailActivity.ARG_SESSAO_ID,
                        new Gson().toJson(holder.mItem)
                );

                itemClicked.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        public SessaoContent.SessaoItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
