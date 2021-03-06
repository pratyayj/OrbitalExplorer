package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilterResultsActivity extends AppCompatActivity {

    private TrailInformationRVAdapter trailInformationRVAdapter;
    private RecyclerView recyclerViewTrailInformation;
    private List<TrailInformation> trailInformationList;

    private DatabaseReference firebaseDatabase;
    private String duration;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_results);

        Intent intent = getIntent();
        duration = intent.getStringExtra("duration");
        type = intent.getStringExtra("type");

        toolbarAssignment(R.drawable.ic_arrow_back_grey_24dp);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        trailInformationList = new ArrayList<>();

        recyclerViewTrailInformation =  findViewById(R.id.trailInformationRV);
        recyclerViewTrailInformation.setHasFixedSize(true);
        trailInformationRVAdapter = new TrailInformationRVAdapter(this, trailInformationList);
        recyclerViewTrailInformation.setAdapter(trailInformationRVAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerViewTrailInformation.setLayoutManager(llm);

        generateTrailsFromFirebase(new TrailInformationCallback() {
            @Override
            public void onCallBack(TrailInformation trailInformation) {
                trailInformationList.add(trailInformation);
                trailInformationRVAdapter.notifyDataSetChanged();
            }
        });
    }

    public void generateTrailsFromFirebase(final TrailInformationCallback trailInformationCallback) {
        DatabaseReference ref = firebaseDatabase.child(duration).child(type);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    TrailInformation ti = ds.getValue(TrailInformation.class);
                    trailInformationCallback.onCallBack(ti);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
