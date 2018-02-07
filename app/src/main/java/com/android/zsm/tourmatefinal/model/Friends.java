package com.android.zsm.tourmatefinal.model;

import java.io.Serializable;

/**
 * Created by SAMIM on 2/3/2018.
 */

public class Friends implements Serializable {
    private String friendId;
    private String eventid;
    private String friendName;
    private String friendPhone;
    private String friendEmail;

    public Friends() {
    }

    public Friends(String friendId, String eventid, String friendName, String friendPhone, String friendEmail) {
        this.friendId = friendId;
        this.eventid = eventid;
        this.friendName = friendName;
        this.friendPhone = friendPhone;
        this.friendEmail = friendEmail;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendPhone() {
        return friendPhone;
    }

    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }
}
