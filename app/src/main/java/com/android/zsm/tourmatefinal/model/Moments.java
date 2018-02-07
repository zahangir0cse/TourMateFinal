package com.android.zsm.tourmatefinal.model;

import java.io.Serializable;

public class Moments implements Serializable {
    private String momentsId;
    private  String eventid;
    private String photourl;
    private String captions;

    public Moments(String momentsId, String eventid, String photourl) {
        this.momentsId = momentsId;
        this.eventid = eventid;
        this.photourl = photourl;
    }

    public Moments(String momentsId, String captions) {
        this.momentsId = momentsId;
        this.captions = captions;
    }

    public Moments(String momentsId, String eventid, String photourl, String captions) {
        this.momentsId = momentsId;
        this.eventid = eventid;
        this.photourl = photourl;
        this.captions = captions;
    }

    public String getMomentsId() {
        return momentsId;
    }

    public void setMomentsId(String momentsId) {
        this.momentsId = momentsId;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getCaptions() {
        return captions;
    }

    public void setCaptions(String captions) {
        this.captions = captions;
    }
}
