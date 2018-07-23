package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * This is the class that binds the data to the RecyclerView layout's specific Views. It retrieves
 * data and generates View objects based on the data. These View objects populate any adapter view.
 */
public class TrailGroupRVAdapter extends RecyclerView.Adapter<TrailGroupRVAdapter.TrailGroupHolder> {

    // List that stores the trail groups.
    private List<TrailGroup> trailGroups;
    // The context that the adpater is used in.
    private Context mContext;
    // The storage reference from which the image data is retrieved.
    private StorageReference photoStorageReference;


    public TrailGroupRVAdapter(Context context, List<TrailGroup> trailGroups){
        this.mContext = context;
        this.trailGroups = trailGroups;
    }

    /**
     * Inflation with the layout parameters (the trail_group_card_layout) happens here.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position - in this case it is the RecyclerView view.
     * @param viewType Not used.
     * @return Returns the ViewHolder.
     */
    @Override
    @NonNull
    public TrailGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trail_group_card_layout, parent, false);
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
    public void onBindViewHolder(@NonNull final TrailGroupHolder trailGrouperHolder, int i) {
        final TrailGroup trailGroup = trailGroups.get(i);
        trailGrouperHolder.groupHeader.setText(trailGroup.getHeader());
        trailGrouperHolder.groupDescription.setText(trailGroup.getDescription());
        photoStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(trailGroup.getPhotouri());
        Glide.with(mContext)
                .load(photoStorageReference)
                .into(trailGrouperHolder.groupPhoto);

        trailGrouperHolder.setRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                String trailGroupHeader = trailGroup.getHeader();
                Intent intent = new Intent(mContext, GroupDisplayActivity.class);
                intent.putExtra("header", trailGroupHeader);
                mContext.startActivity(intent);
            }
        });
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
    public static class TrailGroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView groupCardView;
        TextView groupHeader;
        TextView groupDescription;
        ImageView groupPhoto;

        RecyclerViewClickListener recyclerViewClickListener;

        TrailGroupHolder(View view) {
            super(view);
            groupCardView = itemView.findViewById(R.id.cardViewMain);
            groupHeader = itemView.findViewById(R.id.header);
            groupDescription = itemView.findViewById(R.id.trailInfoDescription);
            groupPhoto = itemView.findViewById(R.id.photo);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onClick(v, getAdapterPosition());
        }

        /**
         * This is used to set the listener for the RecyclerView.
         * @param rvcl The RecyclerViewListener to be set for this holder.
         */
        protected void setRecyclerViewClickListener(RecyclerViewClickListener rvcl) {
            this.recyclerViewClickListener = rvcl;
        }
    }


}
