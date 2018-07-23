package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllTrailsActivity extends AppCompatActivity {

    // Button that when clicked takes you to the filter page.
    private MenuItem mFilterButton;

    // Holds the DrawerLayout (which contains the NavigationView.
    private DrawerLayout mDrawerLayout;

    // The following are variables that are related to the RecyclerView.
    private RecyclerView recyclerViewAllTrails;
    // The All Trails' RV Adapter.
    private TrailInformationRVAdapter allTrailsRVAdapter;
    // The list of TrailGroup.
    private List<TrailInformation> allTrailsList;

    // The Firebase Database reference.
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trails);

        toolbarAssignment(R.drawable.ic_menu_grey_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Assigns the Firebase Database for app to the local variable firebaseDatabase.
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Creates an array list of all the trail groups.
        allTrailsList = new ArrayList<TrailInformation>();

        /* All the trail groups will be shown in a RecyclerView. This here is the setup of
        of the layout and adapters that will be used.
         */
        recyclerViewAllTrails =  findViewById(R.id.recyclerViewAllTrails);
        recyclerViewAllTrails.setHasFixedSize(true);
        allTrailsRVAdapter = new TrailInformationRVAdapter(this, allTrailsList);
        recyclerViewAllTrails.setAdapter(allTrailsRVAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerViewAllTrails.setLayoutManager(llm);

        generateTrailsFromFirebase(new TrailInformationCallback() {
            @Override
            public void onCallBack(TrailInformation trailInformation) {
                allTrailsList.add(trailInformation);
                allTrailsRVAdapter.notifyDataSetChanged();
            }
        });


        /*
         * This section assigns a listener to the NavigationView's items.
         */
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int menuId = item.getItemId();

                switch(menuId) {
                    case R.id.allTrails:
                        item.setChecked(false);
                        break;
                    case R.id.signout:
                        userSignout();
                        item.setChecked(false);
                        break;
                    case R.id.filterDrawer:
                        Intent intent = new Intent(AllTrailsActivity.this, FilterPageActivity.class);
                        startActivity(intent);
                        item.setChecked(false);
                        break;
                    case R.id.home:
                        Intent intent1 = new Intent(AllTrailsActivity.this, MainActivity.class);
                        startActivity(intent1);
                        item.setChecked(false);
                        break;
                    case R.id.history:
                        Intent intent2 = new Intent(AllTrailsActivity.this, UserHistoryActivity.class);
                        startActivity(intent2);
                        item.setChecked(false);
                        break;
                }
                return false;
            }
        });

        setupDrawerHeader(navigationView);

    }

    /**
     * This method inflates the Options Menu in the toolbar.
     * @param menu The menu to be inflated.
     * @return Returns true if the menu is inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        mFilterButton = menu.findItem(R.id.filterMenu);

        return super.onCreateOptionsMenu(menu);
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

    /**
     * To sign user out.
     */
    public void userSignout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(AllTrailsActivity.this, SigninActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * This method deals with the appropriate action to be taken when a MenuItem is clicked.
     * @param item The MenuItem that is selected.
     * @return Returns true if the item is successfully selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.filterMenu:
                Intent intent = new Intent(AllTrailsActivity.this, FilterPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void generateTrailsFromFirebase(final TrailInformationCallback trailInformationCallback) {
        List<DatabaseReference> dbList = new ArrayList<>();
        DatabaseReference refA = firebaseDatabase.child("long").child("nature");
        DatabaseReference refB = firebaseDatabase.child("medium").child("a bit of both");
        DatabaseReference refC = firebaseDatabase.child("medium").child("nature");
        DatabaseReference refD = firebaseDatabase.child("long").child("urban");
        DatabaseReference refE = firebaseDatabase.child("short").child("nature");
        DatabaseReference refF = firebaseDatabase.child("short").child("urban");
        DatabaseReference refG = firebaseDatabase.child("short").child("a bit of both");
        dbList.add(refA);
        dbList.add(refB);
        dbList.add(refC);
        dbList.add(refD);
        dbList.add(refE);
        dbList.add(refF);
        dbList.add(refG);
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

    /**
     * Setting user's name as header text and user's profile picture in drawer.
     * @param navigationView The navigation view whose header is to be set.
     */
    public void setupDrawerHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderName = headerView.findViewById(R.id.headerName);
        ImageView navHeaderPicture = headerView.findViewById(R.id.headerProfilePicture);
        navHeaderName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Uri photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String originalPieceOfUrl = "s96-c/photo.jpg";
        String newPieceOfUrlToAdd = "s400-c/photo.jpg";
        String photoPath = photoUrl.toString();

        String newResImage = photoPath.replace(originalPieceOfUrl, newPieceOfUrlToAdd);

        Glide.with(this)
                .load(newResImage)
                .into(navHeaderPicture);
    }

}
