package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrailHistoryRVAdapter extends RecyclerView.Adapter<TrailHistoryRVAdapter.TrailHistoryHolder> {

    // List that stores all of the user's trail history.
    private List<TrailHistory> trailHistoryList;
    // The context that the adpater is used in.
    private Context mContext;
    // The storage reference from which the image data is retrieved.
    private StorageReference photoStorageReference;

    public TrailHistoryRVAdapter(Context context, List<TrailHistory> trailHistoryList){
        this.mContext = context;
        this.trailHistoryList = trailHistoryList;
    }

    /**
     * Inflation with the layout parameters (the trail_information_card_layout) happens here.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position - in this case it is the RecyclerView view.
     * @param viewType Not used.
     * @return Returns the ViewHolder.
     */
    @Override
    public TrailHistoryRVAdapter.TrailHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trail_history_card_layout, parent, false);
        TrailHistoryRVAdapter.TrailHistoryHolder thh = new TrailHistoryRVAdapter.TrailHistoryHolder(v);
        return thh;
    }

    /**
     * Unused ViewHolder's are filled with data here. ViewHolders that go off screen are replaced
     * with new data.
     * @param trailHistoryHolder Holder that keeps all the Views together.
     * @param i Position i in the list of TrailGroup.
     */
    @Override
    public void onBindViewHolder(final TrailHistoryRVAdapter.TrailHistoryHolder trailHistoryHolder, int i) {
        final TrailHistory trailHistory = trailHistoryList.get(i);
        trailHistoryHolder.historyTitle.setText(trailHistory.getTitle());
        photoStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(trailHistory.getPhotouri());
        Glide.with(mContext)
                .load(photoStorageReference)
                .into(trailHistoryHolder.historyPhoto);

        trailHistoryHolder.setRecyclerViewClickListener(new RecyclerViewClickListener() {
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
        return trailHistoryList.size();
    }

    /**
     * The holder class that keeps all the Views together.
     */
    public static class TrailHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView informationCardView;
        TextView historyTitle;
        ImageView historyPhoto;

        RecyclerViewClickListener recyclerViewClickListener;

        TrailHistoryHolder(View view) {
            super(view);
            informationCardView = itemView.findViewById(R.id.cardViewTH);
            historyTitle = itemView.findViewById(R.id.trailHistoryTitle);
            historyPhoto = itemView.findViewById(R.id.trailHistoryPhoto);
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
