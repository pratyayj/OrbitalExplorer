package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TrailInformationRVAdapter extends RecyclerView.Adapter<TrailInformationRVAdapter.TrailInformationHolder> {

    // List that stores the all of each individual trail's information.
    private List<TrailInformation> trailInformationList;
    // The context that the adpater is used in.
    private Context mContext;
    // The storage reference from which the image data is retrieved.
    private StorageReference photoStorageReference;

    public TrailInformationRVAdapter(Context context, List<TrailInformation> trailInformationList){
        this.mContext = context;
        this.trailInformationList = trailInformationList;
    }

    /**
     * Inflation with the layout parameters (the trail_information_card_layout) happens here.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position - in this case it is the RecyclerView view.
     * @param viewType Not used.
     * @return Returns the ViewHolder.
     */
    @Override
    public TrailInformationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trail_information_card_layout, parent, false);
        TrailInformationHolder tih = new TrailInformationRVAdapter.TrailInformationHolder(v);
        return tih;
    }

    /**
     * Unused ViewHolder's are filled with data here. ViewHolders that go off screen are replaced
     * with new data.
     * @param trailInformationHolder Holder that keeps all the Views together.
     * @param i Position i in the list of TrailGroup.
     */
    @Override
    public void onBindViewHolder(TrailInformationHolder trailInformationHolder, int i) {
        final TrailInformation trailInformation = trailInformationList.get(i);
        trailInformationHolder.informationTitle.setText(trailInformation.getTitle());
        trailInformationHolder.informationDescription.setText(trailInformation.getDescription());
        trailInformationHolder.informationRating.setRating(trailInformation.getRating());
        photoStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(trailInformation.getPhotouri());
        Glide.with(mContext)
                .load(photoStorageReference)
                .into(trailInformationHolder.informationPhoto);


        trailInformationHolder.setRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                // TO ADD ACTION TO BE TAKEN WHEN CARD IS CLICKED.
                Toast.makeText(mContext, "CLICKED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return trailInformationList.size();
    }

    /**
     * The holder class that keeps all the Views together.
     */
    public static class TrailInformationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView informationCardView;
        TextView informationTitle;
        TextView informationDescription;
        RatingBar informationRating;
        ImageView informationPhoto;

        RecyclerViewClickListener recyclerViewClickListener;

        TrailInformationHolder(View view) {
            super(view);
            informationCardView = itemView.findViewById(R.id.cardViewTI);
            informationTitle = itemView.findViewById(R.id.trailInfoTitle);
            informationDescription = itemView.findViewById(R.id.trailInfoDescription);
            informationPhoto = itemView.findViewById(R.id.trailInfoPhoto);
            informationRating = itemView.findViewById(R.id.ratingBar);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onClick(v, getAdapterPosition());
        }

        public void setRecyclerViewClickListener(RecyclerViewClickListener rvcl) {
            this.recyclerViewClickListener = rvcl;
        }
    }

}
