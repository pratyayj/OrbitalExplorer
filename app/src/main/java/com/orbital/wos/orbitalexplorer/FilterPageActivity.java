package com.orbital.wos.orbitalexplorer;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class FilterPageActivity extends AppCompatActivity {

    // This RadioGroup holds all the radio buttons that relate to duration.
    private RadioGroup mRadioGroupDuration;
    // This RadioGroup holds all the radio buttons that relate to type.
    private RadioGroup mRadioGroupType;
    // This Button is assigned for the done button which
    private Button buttonDone;

    // This holds the string which states the duration of trail.
    private String durationToSend;
    // This holds the string which states the type of trail.
    private String typeToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        // TO MAKE THIS MODULAR BY CREATING SEPARATE CLASS.
        toolbarAssignment(R.drawable.ic_arrow_back_grey_24dp);

        mRadioGroupDuration = findViewById(R.id.radioGroupDuration);
        mRadioGroupType = findViewById(R.id.radioGroupType);
        buttonDone = findViewById(R.id.buttonDone);

        /*
         * This sets a listener for the done button once the user has finished selecting the filter
         * options.
         */
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (durationToSend == null && typeToSend == null) {
                    Toast.makeText(getApplicationContext(), "Please select a duration and type."
                            , Toast.LENGTH_SHORT).show();
                } else if (durationToSend == null) {
                    Toast.makeText(getApplicationContext(), "Please select a duration."
                            , Toast.LENGTH_SHORT).show();
                } else if (typeToSend == null) {
                    Toast.makeText(getApplicationContext(), "Please select a type."
                            , Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(FilterPageActivity.this, FilterResultsActivity.class);
                    intent.putExtra("duration", durationToSend);
                    intent.putExtra("type", typeToSend);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        /*
         * This sets a listener for the RadioButtons in the RadioGroup for duration.
         */
        mRadioGroupDuration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.durationA) {
                    Toast.makeText(getApplicationContext(), "You've selected a " +
                                    "short duration trail.", Toast.LENGTH_SHORT).show();
                    durationToSend = "short";
                } else if (checkedId == R.id.durationB) {
                    Toast.makeText(getApplicationContext(), "You've selected a " +
                            "medium duration trail.", Toast.LENGTH_SHORT).show();
                    durationToSend = "medium";
                } else if (checkedId == R.id.durationC) {
                    Toast.makeText(getApplicationContext(), "You've selected a " +
                            "long duration trail.", Toast.LENGTH_SHORT).show();
                    durationToSend = "long";
                }
            }
        });

        /*
         * This sets a listener for the RadioButtons in the RadioGroup for duration.
         */
        mRadioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.typeA) {
                    Toast.makeText(getApplicationContext(), "You've selected a " +
                            "nature trail.", Toast.LENGTH_SHORT).show();
                    typeToSend = "nature";
                } else if (checkedId == R.id.typeB) {
                    Toast.makeText(getApplicationContext(), "You've selected " +
                            "an urban trail.", Toast.LENGTH_SHORT).show();
                    typeToSend = "urban";
                } else if (checkedId == R.id.typeC) {
                    Toast.makeText(getApplicationContext(), "So you want a " +
                            "bit of both, huh!", Toast.LENGTH_SHORT).show();
                    typeToSend = "a bit of both";
                }
            }
        });

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