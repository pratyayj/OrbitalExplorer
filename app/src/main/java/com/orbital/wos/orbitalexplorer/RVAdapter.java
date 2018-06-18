package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TrailGrouperHolder> {

    private List<TrailGrouper> trailGroups;
    private Context mContext;


    public RVAdapter(Context context, List<TrailGrouper> trailGroups){
        this.mContext = context;
        this.trailGroups = trailGroups;
    }


    @Override
    public TrailGrouperHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        TrailGrouperHolder tgh = new TrailGrouperHolder(v);
        return tgh;
    }

    @Override
    public void onBindViewHolder(TrailGrouperHolder trailGrouperHolder, int i) {
        final TrailGrouper trailGrouper = trailGroups.get(i);
        trailGrouperHolder.header.setText(trailGrouper.getHeader());
        trailGrouperHolder.description.setText(trailGrouper.getDescription());

        // TO CREATE ONCLICKLISTENER to go to grouped views of trails.
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return trailGroups.size();
    }

    public static class TrailGrouperHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView header;
        TextView description;
        ImageView photo;

        TrailGrouperHolder(View view) {
            super(view);
            cv = itemView.findViewById(R.id.cardViewMain);
            header = itemView.findViewById(R.id.header);
            description = itemView.findViewById(R.id.description);
            photo = itemView.findViewById(R.id.photo);
        }

    }


}
