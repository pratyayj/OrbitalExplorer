package com.orbital.wos.orbitalexplorer;

import android.content.Context;
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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button signout;
    private DrawerLayout mDrawerLayout;
    private MenuItem mFilter;

    private DatabaseReference firebaseDatabase;

    // The following are variables that are related to the RecyclerView.
    private RecyclerView mRvTrailGrouper;
    private RVAdapter rvAdapter;
    private List<TrailGroup> trailGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * This group of method calls and assignments relate to
         * the ActionBar and ToolBar.
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_grey_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int menuId = item.getItemId();

                switch(menuId) {
                    case R.id.signout:
                        userSignout();
                        break;
                }
                return false;
            }
        });


        // Setting user's name as header text in drawer.
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderName = headerView.findViewById(R.id.name);
        navHeaderName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());




        // signout = findViewById(R.id.buttonSignout);

        // Assigns the Firebase Database for app to the local variable firebaseDatabase.
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Creates an array list of all the trail groups.
        trailGroups = new ArrayList<TrailGroup>();

        /* All the trail groups will be shown in a RecyclerView. This here is the setup of
        of the layout and adapters that will be used.
         */
        mRvTrailGrouper =  findViewById(R.id.rv);
        mRvTrailGrouper.setHasFixedSize(true);
        rvAdapter = new RVAdapter(this, trailGroups);
        mRvTrailGrouper.setAdapter(rvAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRvTrailGrouper.setLayoutManager(llm);

        /*
        The getFirebaseData method helps to create to add each group of trails to the List object
        holding all these trails.
         */
        getFirebaseData(new TrailGroupsCallback() {
            @Override
            public void onCallBack(TrailGroup trailGroup) {
                trailGroups.add(trailGroup);
                Collections.sort(trailGroups, new TrailGroupComparator());
                rvAdapter.notifyDataSetChanged();
            }
        });

        /* signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignout();
            }
        }); */

    }

    /**
     * This method takes in the
     * @param trailGroupsCallback Callback object that has the onCallBack method (defined in
     *                            the method call in onCreate) that runs when each
     *                            TrailGrouper object is passed in.
     */
    private void getFirebaseData(final TrailGroupsCallback trailGroupsCallback) {
        DatabaseReference trailGroups = firebaseDatabase.child("groups");
        trailGroups.addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.filter:
                Intent intent = new Intent(MainActivity.this, SearchPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        mFilter = menu.findItem(R.id.filter);

        return super.onCreateOptionsMenu(menu);
    }

}
