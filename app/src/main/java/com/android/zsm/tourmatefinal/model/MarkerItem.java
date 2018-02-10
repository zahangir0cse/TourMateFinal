package com.android.zsm.tourmatefinal.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by BITM Trainer 601 on 1/11/2018.
 */

public class MarkerItem implements ClusterItem {

    private LatLng postion;
    private String title;
    private String snippet;

    public MarkerItem(LatLng postion) {
        this.postion = postion;
    }

    public MarkerItem(LatLng postion, String title, String snippet) {
        this.postion = postion;
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return postion;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
