package com.orbital.wos.orbitalexplorer;

public class Trail {
    private String trailId;
    private String trailName;
    private String trailRating;
    private String trailDescription;

    public Trail(){
    }

    public Trail(String trailId, String trailName, String trailRating, String trailDescription) {
        this.trailId = trailId;
        this.trailName = trailName;
        this.trailRating = trailRating;
        this.trailDescription = trailDescription;
    }

    public String getTrailId() {
        return trailId;
    }

    public String getTrailName() {
        return trailName;
    }

    public String getTrailRating() {
        return trailRating;
    }

    public String getTrailDescription() {
        return trailDescription;
    }
}