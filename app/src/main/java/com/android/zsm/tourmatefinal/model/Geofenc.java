package com.android.zsm.tourmatefinal.model;

import java.io.Serializable;

public class Geofenc implements Serializable {
    private  String fencid;
    private String eventid;
    private  String fenceName;
    private  double fencelat;
    private  double fenceLon;

    public Geofenc() {
    }

    public Geofenc(String fencid,String eventid, String fenceName, double fencelat, double fenceLon) {
        this.fencid = fencid;
        this.eventid = eventid;
        this.fenceName = fenceName;
        this.fencelat = fencelat;
        this.fenceLon = fenceLon;
    }

    public String getFencid() {
        return fencid;
    }

    public void setFencid(String fencid) {
        this.fencid = fencid;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public double getFencelat() {
        return fencelat;
    }

    public void setFencelat(double fencelat) {
        this.fencelat = fencelat;
    }

    public double getFenceLon() {
        return fenceLon;
    }

    public void setFenceLon(double fenceLon) {
        this.fenceLon = fenceLon;
    }
}
