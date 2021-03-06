package com.orbital.wos.orbitalexplorer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that models the TrailGroup object.
 */
public class TrailGroup {
    private String header;
    private String description;
    private String photouri;
    private int index;

    public TrailGroup(String inputHeader, String inputDescription, String inputPhotouri, int inputIndex) {
        header = inputHeader;
        description = inputDescription;
        photouri = inputPhotouri;
        index = inputIndex;
    }

    public TrailGroup() {

    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotouri(String photouri) {
        this.photouri = photouri;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotouri() {
        return photouri;
    }

    public int getIndex() {
        return index;
    }
}
