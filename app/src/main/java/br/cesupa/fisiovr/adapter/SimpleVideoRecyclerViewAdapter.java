package br.cesupa.fisiovr.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import junit.framework.Test;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.cesupa.fisiovr.detail.ItemDetailActivity;
import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.detail.VideoDetailActivity;
import br.cesupa.fisiovr.dummy.VideoContent;

/**
 * Created by luis.portugal on 01/08/2017.
 */

public class SimpleVideoRecyclerViewAdapter extends RecyclerView.Adapter<SimpleVideoRecyclerViewAdapter.ViewHolder> {

    private Context c;

    private List<VideoContent.VideoItem> mValues = new ArrayList<VideoContent.VideoItem>();

    public SimpleVideoRecyclerViewAdapter(){}

    public SimpleVideoRecyclerViewAdapter(List<VideoContent.VideoItem> list){
        this.mValues = list;
    }

    public void addItem(VideoContent.VideoItem item) {
        mValues.add(item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_content, parent, false);

        this.c = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        Picasso.with(this.c)
                .load(mValues.get(position).thumbnail)
                .into(holder.image);

        holder.title.setText(mValues.get(position).title);
        holder.description.setText(mValues.get(position).description);
        holder.views.setText(mValues.get(position).view_count);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View itemClicked) {
                Intent intent = new Intent(itemClicked.getContext(), VideoDetailActivity.class);
                intent.putExtra(
                        VideoDetailActivity.ARG_VIDEO_ID,
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
        final ImageView image;
        final TextView title;
        final TextView description;
        final TextView views;
        public VideoContent.VideoItem mItem;

        ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.image = (ImageView) itemView.findViewById(R.id.imageVideo);
            this.title = (TextView) itemView.findViewById(R.id.titleVideo);
            this.description = (TextView) itemView.findViewById(R.id.descriptionVideo);
            this.views = (TextView) itemView.findViewById(R.id.viewsVideo);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
