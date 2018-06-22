package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class SearchPageActivity extends AppCompatActivity {

    private TextView testingTextView;
    private DrawerLayout mDrawerLayout;
    private RadioGroup mRadioGroupDuration;
    private RadioGroup mRadioGroupType;
    private Button buttonDone;


    private String durationToSend;
    private String typeToSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        // TO MAKE THIS MODULAR BY CREATING SEPARATE CLASS.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_grey_24dp);

        mRadioGroupDuration = findViewById(R.id.radioGroupDuration);
        mRadioGroupType = findViewById(R.id.radioGroupType);
        buttonDone = findViewById(R.id.buttonDone);



        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchPageActivity.this, FilterResults.class);
                intent.putExtra("duration", durationToSend);
                intent.putExtra("type", typeToSend);
                startActivity(intent);
            }
        });

        mRadioGroupDuration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.durationA) {
                    Toast.makeText(getApplicationContext(), "Short", Toast.LENGTH_SHORT).show();
                    durationToSend = "short";
                } else if (checkedId == R.id.durationB) {
                    Toast.makeText(getApplicationContext(), "Medium", Toast.LENGTH_SHORT).show();
                    durationToSend = "medium";
                } else if (checkedId == R.id.durationC) {
                    Toast.makeText(getApplicationContext(), "Long", Toast.LENGTH_SHORT).show();
                    durationToSend = "long";
                }
            }
        });

        mRadioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.typeA) {
                    Toast.makeText(getApplicationContext(), "Nature", Toast.LENGTH_SHORT).show();
                    typeToSend = "nature";
                } else if (checkedId == R.id.typeB) {
                    Toast.makeText(getApplicationContext(), "Urban", Toast.LENGTH_SHORT).show();
                    typeToSend = "urban";
                } else if (checkedId == R.id.typeC) {
                    Toast.makeText(getApplicationContext(), "A bit of both!", Toast.LENGTH_SHORT).show();
                    typeToSend = "a bit of both";
                }
            }
        });

    }

    /**
     * To sign user out.
     */
    public void userSignout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SearchPageActivity.this, SigninActivity.class);
        startActivity(intent);
        finish();
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
