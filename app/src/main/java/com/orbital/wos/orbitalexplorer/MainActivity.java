package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Button that when clicked signs the user out.
    private Button signout;
    // Holds the DrawerLayout (which contains the NavigationView.
    private DrawerLayout mDrawerLayout;
    // Button that when clicked takes you to the filter page.
    private MenuItem mFilterButton;

    // The Firebase Database reference.
    private DatabaseReference firebaseDatabase;

    // The following are variables that are related to the RecyclerView.
    private RecyclerView recyclerViewTrailGroup;
    // The TrailGroup's RV Adapter.
    private TrailGroupRVAdapter trailGroupRvAdapter;
    // The list of TrailGroup.
    private List<TrailGroup> trailGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * This group of method calls and assignments relate to
         * the ActionBar and ToolBar.
         */
        toolbarAssignment(R.drawable.ic_menu_grey_24dp);
        /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_grey_24dp); */
        mDrawerLayout = findViewById(R.id.drawer_layout);

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
                    case R.id.home:
                        item.setChecked(false);
                        break;
                    case R.id.signout:
                        userSignout();
                        item.setChecked(false);
                        break;
                    case R.id.filterDrawer:
                        Intent intent = new Intent(MainActivity.this, FilterPageActivity.class);
                        startActivity(intent);
                        item.setChecked(false);
                        break;
                    case R.id.allTrails:
                        Intent intent1 = new Intent(MainActivity.this, AllTrailsActivity.class);
                        startActivity(intent1);
                        item.setChecked(false);
                        break;
                    case R.id.history:
                        Intent intent2 = new Intent(MainActivity.this, UserHistoryActivity.class);
                        startActivity(intent2);
                        item.setChecked(false);
                        break;
                }
                return false;
            }
        });


        // Setting user's name as header text in drawer.
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderName = headerView.findViewById(R.id.name);
        navHeaderName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        // Assigns the Firebase Database for app to the local variable firebaseDatabase.
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Creates an array list of all the trail groups.
        trailGroups = new ArrayList<TrailGroup>();

        /* All the trail groups will be shown in a RecyclerView. This here is the setup of
        of the layout and adapters that will be used.
         */
        recyclerViewTrailGroup =  findViewById(R.id.recyclerViewTrailGroup);
        recyclerViewTrailGroup.setHasFixedSize(true);
        trailGroupRvAdapter = new TrailGroupRVAdapter(this, trailGroups);
        recyclerViewTrailGroup.setAdapter(trailGroupRvAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerViewTrailGroup.setLayoutManager(llm);

        /*
        The getFirebaseData method helps to create to add each group of trails to the List object
        holding all these trails.
         */
        getFirebaseData(new TrailGroupsCallback() {
            @Override
            public void onCallBack(TrailGroup trailGroup) {
                trailGroups.add(trailGroup);
                Collections.sort(trailGroups, new TrailGroupComparator());
                trailGroupRvAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * This method takes in the Callback object that is created and which will be run when this
     * method is completed.
     * @param trailGroupsCallback Callback object that has the onCallBack method (defined in
     *                            the method call in onCreate) that runs when each
     *                            TrailGrouper object is passed in.
     */
    private void getFirebaseData(final TrailGroupsCallback trailGroupsCallback) {
        // Only data related to the the grouping of trails are retrieved
        DatabaseReference trailGroupsDatabase = firebaseDatabase.child("groups");
        trailGroupsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    TrailGroup tg = new TrailGroup();
                    tg = dataSnap.getValue(TrailGroup.class);
                    trailGroupsCallback.onCallBack(tg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * To sign user out.
     */
    public void userSignout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, SigninActivity.class);
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
                Intent intent = new Intent(MainActivity.this, FilterPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

}