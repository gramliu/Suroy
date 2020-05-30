package com.dyip.suroy.rider.model;

import com.dyip.suroy.rider.Constants;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashSet;

public class SuroyUser {

    private String hash;
    private String display_name;
    private HashSet<String> visited;

    public SuroyUser(String hash, String display_name, HashSet<String> set) {
        this.hash = hash;
        this.display_name = display_name;
        this.visited = new HashSet<>();
    }

    public String getDisplayName() {
        return display_name;
    }

    public int getVisitCount() {
        return visited.size();
    }

    public String getHash() {
        return hash;
    }

    public void visitPlace(Iterable<Place> places) {
        int oSize = visited.size();
        for (Place place : places) {
            visited.add(place.getID());
        }
        int nSize = visited.size();
        if (oSize != nSize) {
            ArrayList<String> list = new ArrayList<>(visited);
            DatabaseReference ref = Constants.firebase.getReference("users")
                    .child("riders").child(Constants.user.getHash()).child("visited");
            ref.setValue(list);
            Constants.visited.setText("Places visited: " + nSize);
        }
    }

}
