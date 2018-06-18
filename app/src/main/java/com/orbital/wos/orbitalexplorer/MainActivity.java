package com.orbital.wos.orbitalexplorer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextRating;
    EditText editTextDescription;
    Button buttonAddTrail;
//    ListView listViewTrails;
//
//    //a list to store all the artist from firebase database
//    List<Trail> trails;

    //our database reference object
    DatabaseReference databaseTrails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting the reference of trails node
        databaseTrails = FirebaseDatabase.getInstance().getReference("trails");

        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextRating = (EditText) findViewById(R.id.editTextRating);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
//        listViewTrails = (ListView) findViewById(R.id.listViewTrails);

        buttonAddTrail = (Button) findViewById(R.id.buttonAddTrail);

//        //list to store trails
//        trails = new ArrayList<>();


        //adding an onclicklistener to button
        buttonAddTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addTrail()
                //the method is defined below
                //this method is actually performing the write operation
                addTrail();
            }
        });
    }

    /*
     * This method is saving a new trail to the
     * Firebase Realtime Database
     * */
    private void addTrail() {
        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String rating = editTextRating.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(rating) && !TextUtils.isEmpty(description)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Trail
            String id = databaseTrails.push().getKey();

            //creating an Trail Object
            Trail trail = new Trail(id, name, rating, description);

            //Saving the Trail
            databaseTrails.child(id).setValue(trail);

            //setting edittext to blank again
            editTextName.setText("");
            editTextRating.setText("");
            editTextDescription.setText("");

            //displaying a success toast
            Toast.makeText(this, "Trail added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please fill in the missing blanks", Toast.LENGTH_LONG).show();
        }
    }
}