package com.dyip.suroy.rider.model;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private String name;
    private LatLng ne, sw, coords;
    private String id;
    private String photo_id;
    private int type;

    public Place(String name, String id, String photo_id, LatLng ne, LatLng sw, LatLng coords, int type) {
        this.name = name;
        this.id = id;
        this.photo_id = photo_id;
        this.ne = ne;
        this.sw = sw;
        this.coords = coords;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return this.id;
    }

    public String getPhotoID() {
        return photo_id;
    }

    public LatLng getCoords() {
        return coords;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Place) {
            return id.equals(((Place) o).getID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
