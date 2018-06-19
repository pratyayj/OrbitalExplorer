package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * This is the class that binds the data to the RecyclerView layout's specific Views. It retrieves
 * data and generates View objects based on the data. These View objects populate any adapter view.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TrailGroupHolder> {

    // List that stores the trail groups.
    private List<TrailGroup> trailGroups;
    // The context that the adpater is used in.
    private Context mContext;
    // The storage reference from which the image data is retrieved.
    private StorageReference photoStorageReference;


    public RVAdapter(Context context, List<TrailGroup> trailGroups){
        this.mContext = context;
        this.trailGroups = trailGroups;
    }

    /**
     * Inflation with the layout parameters (the card_layout) happens here.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position - in this case it is the RecyclerView view.
     * @param viewType Not used.
     * @return Returns the ViewHolder.
     */
    @Override
    public TrailGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        TrailGroupHolder tgh = new TrailGroupHolder(v);
        return tgh;
    }

    /**
     * Unused ViewHolder's are filled with data here. ViewHolders that go off screen are replaced
     * with new data.
     * @param trailGrouperHolder Holder that keeps all the Views together.
     * @param i Position i in the list of TrailGroup.
     */
    @Override
    public void onBindViewHolder(TrailGroupHolder trailGrouperHolder, int i) {
        final TrailGroup trailGroup = trailGroups.get(i);
        trailGrouperHolder.header.setText(trailGroup.getHeader());
        trailGrouperHolder.description.setText(trailGroup.getDescription());
        photoStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(trailGroup.getPhotouri());
        Glide.with(mContext)
                .load(photoStorageReference)
                .into(trailGrouperHolder.photo);
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

    /**
     * The holder class that keeps all the Views together.
     */
    public static class TrailGroupHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView header;
        TextView description;
        ImageView photo;

        TrailGroupHolder(View view) {
            super(view);
            cv = itemView.findViewById(R.id.cardViewMain);
            header = itemView.findViewById(R.id.header);
            description = itemView.findViewById(R.id.description);
            photo = itemView.findViewById(R.id.photo);
        }

    }


}
