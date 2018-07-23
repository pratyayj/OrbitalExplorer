package com.orbital.wos.orbitalexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
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
    public void onBindViewHolder(final TrailInformationHolder trailInformationHolder, int i) {
        final TrailInformation trailInformation = trailInformationList.get(i);
        trailInformationHolder.informationTitle.setText(trailInformation.getTitle());
        trailInformationHolder.informationDescription.setText(trailInformation.getDescription());
        trailInformationHolder.informationDescription.setVisibility(View.GONE);
        trailInformationHolder.informationRating.setRating(trailInformation.getRating());
        photoStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(trailInformation.getPhotouri());
        Glide.with(mContext)
                .load(photoStorageReference)
                .into(trailInformationHolder.informationPhoto);

        trailInformationHolder.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MapsActivity.class);
                double tempLat = trailInformation.getLatitude();
                double tempLong = trailInformation.getLongitude();
                String tempTitle = trailInformation.getTitle();
                intent.putExtra("latitude", tempLat);
                intent.putExtra("longitude", tempLong);
                intent.putExtra("title", tempTitle);
                mContext.startActivity(intent);

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String trailSelectedTitle = trailInformation.getTitle();
                DatabaseReference userdataDBR = FirebaseDatabase.getInstance().getReference().child("userdata");
                final DatabaseReference trailcountDBR = FirebaseDatabase.getInstance().getReference().child("trailcount");

                trailcountDBR.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(trailSelectedTitle).exists()) {
                            Map<String, Object> currentCount = (Map<String, Object>) dataSnapshot.getValue();
                            long currentCountI;
                            currentCountI = (long) currentCount.get(trailSelectedTitle);
                            currentCountI += 1.0;
                            trailcountDBR.child(trailSelectedTitle).setValue(currentCountI);
                        } else {
                            Map<String, Object> trial = new HashMap<>();
                            trial.put(trailSelectedTitle, 1);
                            trailcountDBR.updateChildren(trial);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Map<String, Object> trial = new HashMap<>();
                trial.put("title", trailSelectedTitle);
                trial.put("photouri", trailInformation.getPhotouri());
                trial.put("latitude", trailInformation.getLatitude());
                trial.put("longitude", trailInformation.getLongitude());

                userdataDBR.child(currentUser.getUid()).child(trailSelectedTitle).updateChildren(trial);
            }
        });

        trailInformationHolder.setRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                // TO ADD ACTION TO BE TAKEN WHEN CARD IS CLICKED.
                if (trailInformationHolder.dropDownStatus == -1) {
                    //Toast.makeText(mContext, "CLICKED", Toast.LENGTH_SHORT).show();
                    trailInformationHolder.informationDescription.setVisibility(View.VISIBLE);
                    trailInformationHolder.informationDescription.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                    trailInformationHolder.dropDownArrow.animate().rotationBy(180).setDuration(1).start();
                    trailInformationHolder.dropDownStatus = 1;
                } else {
                    //Toast.makeText(mContext, "CLICKED", Toast.LENGTH_SHORT).show();
                    trailInformationHolder.informationDescription.setVisibility(View.GONE);
                    trailInformationHolder.informationDescription.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    trailInformationHolder.dropDownArrow.animate().rotationBy(180).setDuration(1).start();
                    trailInformationHolder.dropDownStatus = -1;
                }
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
        ImageView dropDownArrow;
        Button goButton;
        int dropDownStatus = -1;

        RecyclerViewClickListener recyclerViewClickListener;

        TrailInformationHolder(View view) {
            super(view);
            informationCardView = itemView.findViewById(R.id.cardViewTI);
            informationTitle = itemView.findViewById(R.id.trailInfoTitle);
            informationDescription = itemView.findViewById(R.id.trailInfoDescription);
            informationPhoto = itemView.findViewById(R.id.trailInfoPhoto);
            informationRating = itemView.findViewById(R.id.ratingBar);
            dropDownArrow = itemView.findViewById(R.id.dropDownArrow);
            goButton = itemView.findViewById(R.id.buttonGo);
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
