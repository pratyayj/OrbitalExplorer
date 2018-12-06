package com.orbital.wos.orbitalexplorer;

import android.app.DialogFragment;
import android.content.Context;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RatingFragment extends DialogFragment {
    static RatingFragment newInstance() {
        return new RatingFragment();
    }

    DatabaseReference firebaseDBR = FirebaseDatabase.getInstance().getReference();

    Context mContext = getActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String trailTitle = getArguments().getString("title");

        View v = inflater.inflate(R.layout.fragment_rating, container, false);
        final RatingBar ratingBar = v.findViewById(R.id.user_dialog_ratingbar);

        Button btn = v.findViewById(R.id.rate_dialog_button);

        final DatabaseReference baseDBR = FirebaseDatabase.getInstance().getReference().child("trailuserdata")
                .child(trailTitle);

        final DatabaseReference totalTrailRatingDBR = FirebaseDatabase.getInstance().getReference().child("trailuserdata")
                .child(trailTitle).child("totalrating");

        final DatabaseReference totalRatersDBR = firebaseDBR.child("trailuserdata")
                .child(trailTitle).child("numberofraters");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalTrailRatingDBR.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("please", "insde on data");
                        long currentRating = dataSnapshot.getValue(Long.class);
                        Log.d("please", Long.toString(currentRating) + "wow I am actually haps");
                        float rating = ratingBar.getRating();
                        baseDBR.child("totalrating").setValue(currentRating + rating);
                        Log.d("please", Float.toString(rating) + "in bar");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                totalRatersDBR.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long currentRatersNumber = dataSnapshot.getValue(Long.class);
                        currentRatersNumber+=1.0;
                        baseDBR.child("numberofraters").setValue(currentRatersNumber);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                dismiss();
            }
        });
        return v;
    }

}
