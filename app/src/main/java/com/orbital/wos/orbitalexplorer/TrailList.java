package com.orbital.wos.orbitalexplorer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TrailList extends ArrayAdapter<Trail> {

    private Activity context;
    private List<Trail> trailList;

    public TrailList(Activity context, List<Trail> trailList) {
        super(context, R.layout.list_layout, trailList);
        this.context = context;
        this.trailList = trailList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewRating = (TextView) listViewItem.findViewById(R.id.textViewRating);
        TextView textViewDescription = (TextView) listViewItem.findViewById(R.id.textViewDescription);

        Trail trail = trailList.get(position);

        textViewName.setText(trail.getTrailName());
        textViewRating.setText(trail.getTrailRating());
        textViewDescription.setText(trail.getTrailDescription());

        return listViewItem;
    }
}
