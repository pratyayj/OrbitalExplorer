package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHistoryActivity extends AppCompatActivity {

    // Button that when clicked takes you to the filter page.
    private MenuItem mFilterButton;

    // Holds the DrawerLayout (which contains the NavigationView.
    private DrawerLayout mDrawerLayout;

    // The following are variables that are related to the RecyclerView.
    private RecyclerView recyclerViewTrailHistory;
    // The User History RV Adapter.
    private TrailHistoryRVAdapter trailHistoryRVAdapter;
    // The list of Trails that make the Users History.
    private List<TrailHistory> trailHistoryList;

    // The Firebase Database reference.
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        toolbarAssignment(R.drawable.ic_menu_grey_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Assigns the Firebase Database for app to the local variable firebaseDatabase.
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Creates an array list of all the user's history of trails.
        trailHistoryList = new ArrayList<TrailHistory>();

        /* All the user's trail history will be shown in a RecyclerView. This here is the setup of
        of the layout and adapters that will be used.
         */
        recyclerViewTrailHistory =  findViewById(R.id.recyclerViewTrailHistory);
        recyclerViewTrailHistory.setHasFixedSize(true);
        trailHistoryRVAdapter = new TrailHistoryRVAdapter(this, trailHistoryList);
        recyclerViewTrailHistory.setAdapter(trailHistoryRVAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerViewTrailHistory.setLayoutManager(llm);

        generateTrailsFromFirebase(new TrailHistoryCallback() {
            @Override
            public void onCallBack(TrailHistory trailHistory) {
                trailHistoryList.add(trailHistory);
                trailHistoryRVAdapter.notifyDataSetChanged();
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
                        Intent intent = new Intent(UserHistoryActivity.this, FilterPageActivity.class);
                        startActivity(intent);
                        item.setChecked(false);
                        break;
                    case R.id.home:
                        Intent intent1 = new Intent(UserHistoryActivity.this, MainActivity.class);
                        finishAffinity();
                        startActivity(intent1);
                        item.setChecked(false);
                        break;
                    case R.id.history:
                        item.setChecked(false);
                        break;
                }
                return false;
            }
        });
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
        Intent intent = new Intent(UserHistoryActivity.this, SigninActivity.class);
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
                Intent intent = new Intent(UserHistoryActivity.this, FilterPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method helps to generate the trails that the user has previously viewed from the
     * Firebase database.
     * @param trailHistoryCallback The callback that will be performed once the data is retrieved.
     */

    public void generateTrailsFromFirebase(final TrailHistoryCallback trailHistoryCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserUid = currentUser.getUid();
        DatabaseReference refA = firebaseDatabase.child("userdata");
        refA.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUserUid).exists()) {
                    for (DataSnapshot ds : dataSnapshot.child(currentUserUid).getChildren()) {
                        TrailHistory th = new TrailHistory();
                        th = ds.getValue(TrailHistory.class);
                        trailHistoryCallback.onCallBack(th);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
