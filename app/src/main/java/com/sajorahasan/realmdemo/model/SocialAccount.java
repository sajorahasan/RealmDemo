package com.sajorahasan.realmdemo.model;

import io.realm.RealmObject;

/**
 * Created by Sajora on 25-04-2017.
 */

public class SocialAccount extends RealmObject {

    private String name;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
