package br.cesupa.fisiovr.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.detail.ItemDetailActivity;
import br.cesupa.fisiovr.dummy.FisioterapeutaContent;

/**
 * Created by luis.portugal on 01/08/2017.
 */

public class SimpleFisioterapeutaRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleFisioterapeutaRecyclerViewAdapter.ViewHolder> {

    private final List<FisioterapeutaContent.FisioterapeutaItem> mValues;

    public SimpleFisioterapeutaRecyclerViewAdapter() {
        mValues = FisioterapeutaContent.ITEMS;
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
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemDetailActivity.class);
                intent.putExtra(ItemDetailActivity.ARG_ITEM_ID, holder.mItem.id);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public FisioterapeutaContent.FisioterapeutaItem mItem;

        public ViewHolder(View view) {
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
