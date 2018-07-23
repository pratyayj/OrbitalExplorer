package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupDisplayActivity extends AppCompatActivity {

    private TrailInformationRVAdapter groupDisplayRVAdapter;
    private RecyclerView recyclerViewGroupDisplay;
    private List<TrailInformation> trailInformationList;

    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_display);

        Intent intent = getIntent();
        String typeOfGroup = intent.getStringExtra("header");

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        trailInformationList = new ArrayList<TrailInformation>();

        recyclerViewGroupDisplay =  findViewById(R.id.groupDisplayRV);
        recyclerViewGroupDisplay.setHasFixedSize(true);
        groupDisplayRVAdapter = new TrailInformationRVAdapter(this, trailInformationList);
        recyclerViewGroupDisplay.setAdapter(groupDisplayRVAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerViewGroupDisplay.setLayoutManager(llm);

        toolbarAssignment(R.drawable.ic_arrow_back_grey_24dp);

        generateTrailsFromFirebase(new TrailInformationCallback() {
            @Override
            public void onCallBack(TrailInformation trailInformation) {
                trailInformationList.add(trailInformation);
                groupDisplayRVAdapter.notifyDataSetChanged();
            }
        }, typeOfGroup);

    }

    protected void toolbarAssignment(int drawable) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(drawable);

    }

    public void generateTrailsFromFirebase(final TrailInformationCallback trailInformationCallback, String typeOfGroup) {
        List<DatabaseReference> dbList = new ArrayList<>();

        if (typeOfGroup.equals("Editor's Picks")) {
            Log.d("fb", "here editor");
            DatabaseReference ref = firebaseDatabase.child("groupsresults").child("Editor Choice");
            dbList.add(ref);
        } else if (typeOfGroup.equals("Fast-game Trails")) {
            Log.d("fb", "here fast");
            DatabaseReference refShortA = firebaseDatabase.child("short").child("nature");
            DatabaseReference refShortB = firebaseDatabase.child("short").child("urban");
            DatabaseReference refShortC = firebaseDatabase.child("short").child("a bit of both");
            dbList.add(refShortA);
            dbList.add(refShortB);
            dbList.add(refShortC);
        } else if (typeOfGroup.equals("New Trails")) {
            DatabaseReference ref = firebaseDatabase.child("groupsresults").child("Newly Added");
            Log.d("fb", "here new");
            dbList.add(ref);
        }

        for (DatabaseReference x : dbList) {
            x.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        TrailInformation ti = ds.getValue(TrailInformation.class);
                        trailInformationCallback.onCallBack(ti);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
