package com.dyip.suroy.driver.model;

public class SuroyUser {

    private String hash;
    private String display_name;
    private String plate_number;
    private int route_code;

    public SuroyUser(String hash, String display_name, String plate_number, int route_code) {
        this.hash = hash;
        this.display_name = display_name;
        this.plate_number = plate_number;
        this.route_code = route_code;
    }

    public String getPlateNumber() {
        return plate_number;
    }

    public int getRouteCode() {
        return route_code;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getHash() {
        return hash;
    }

}
