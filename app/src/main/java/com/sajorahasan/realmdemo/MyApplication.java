package com.sajorahasan.realmdemo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Sajora on 25-04-2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myFirstRealm.realm")
                .build();

        Realm.setDefaultConfiguration(config);
    }

//    public static Realm getAnotherRealm() {
//        RealmConfiguration myOtherConfig = new RealmConfiguration.Builder()
//                .name("myAnotherRealm.realm")
//                .build();
//        Realm myAnotherRealm = Realm.getInstance(myOtherConfig);
//
//        return myAnotherRealm;
//    }

}
