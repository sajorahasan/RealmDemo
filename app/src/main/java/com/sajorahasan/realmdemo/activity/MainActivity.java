package com.sajorahasan.realmdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sajorahasan.realmdemo.R;
import com.sajorahasan.realmdemo.model.SocialAccount;
import com.sajorahasan.realmdemo.model.User;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText etPersonName, etAge, etSocialAccountName, etStatus;

    private Realm myRealm;
    private RealmAsyncTask realmAsyncTask;
    private RealmResults<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPersonName = (EditText) findViewById(R.id.etPersonName);
        etAge = (EditText) findViewById(R.id.etAge);
        etSocialAccountName = (EditText) findViewById(R.id.etSocialAccount);
        etStatus = (EditText) findViewById(R.id.etStatus);

        myRealm = Realm.getDefaultInstance();

        // Another Realm Database
        //Realm myAnotherRealm = MyApplication.getAnotherRealm();
    }

    public void addToRealm_Synchronously(View view) {

        final String id = UUID.randomUUID().toString();
        final String name = etPersonName.getText().toString().trim();
        final int age = Integer.parseInt(etAge.getText().toString().trim());
        final String socialAccountName = etSocialAccountName.getText().toString().trim();
        final String status = etStatus.getText().toString().trim();

//        try {
//            myRealm.beginTransaction();
//
//            myRealm.commitTransaction();
//        } catch (Exception e) {
//            myRealm.cancelTransaction();
//            e.printStackTrace();
//        }

        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SocialAccount socialAccount = realm.createObject(SocialAccount.class);
                socialAccount.setName(socialAccountName);
                socialAccount.setStatus(status);

                User user = realm.createObject(User.class, id);
                user.setName(name);
                user.setAge(age);
                user.setSocialAccount(socialAccount);
                Toast.makeText(MainActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addToRealm_ASynchronously(View view) {

        final String id = UUID.randomUUID().toString();
        final String name = etPersonName.getText().toString().trim();
        final int age = Integer.parseInt(etAge.getText().toString().trim());
        final String socialAccountName = etSocialAccountName.getText().toString().trim();
        final String status = etStatus.getText().toString().trim();

        realmAsyncTask = myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SocialAccount socialAccount = realm.createObject(SocialAccount.class);
                socialAccount.setName(socialAccountName);
                socialAccount.setStatus(status);

                User user = realm.createObject(User.class, id);
                user.setName(name);
                user.setAge(age);
                user.setSocialAccount(socialAccount);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(MainActivity.this, "Error Occurred" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void sampleQueryExample(View view) {

        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).findFirst();
                user.setName("hasan-Sajora");
                user.setAge(21);

                SocialAccount socialAccount = user.getSocialAccount();
                if (socialAccount != null) {
                    socialAccount.setName("Snapchat");
                    socialAccount.setStatus("Available 24/7");
                }

            }
        });


        RealmResults<User> userList = myRealm.where(User.class)
                .between("age", 20, 25)
                .beginGroup()
                .beginsWith("name", "h")
                .or()
                .contains("name", "s")
                .endGroup()
                .findAll()
                .sort("socialAccount.name");
        displayQueriedResult(userList);

    }

    public void displayAllUsers(View view) {
        RealmResults<User> userList = myRealm.where(User.class).findAll();
        Log.d(TAG, "displayAllUsers: userList Size " + userList.size());
        Log.d(TAG, "displayAllUsers: Min Age -->" + userList.min("age").intValue());
        Log.d(TAG, "displayAllUsers: Max Age -->" + userList.max("age").intValue());
        Log.d(TAG, "displayAllUsers: Avg Age -->" + userList.average("age"));
        Log.d(TAG, "displayAllUsers: Sum Age -->" + userList.sum("age").intValue());
        displayQueriedResult(userList);
    }

    public void openDisplayActivity(View view) {
        startActivity(new Intent(MainActivity.this, DisplayActivity.class));
    }

    public void exploreMiscConcepts(View view) {

        userList = myRealm.where(User.class).findAllAsync();
        userList.addChangeListener(userListListner);

//        if (userList.isLoaded())
//            userList.deleteFirstFromRealm();
    }

    RealmChangeListener<RealmResults<User>> userListListner = new RealmChangeListener<RealmResults<User>>() {
        @Override
        public void onChange(RealmResults<User> userList) {
            displayQueriedResult(userList);
        }
    };

    private void displayQueriedResult(RealmResults<User> userList) {
        StringBuilder builder = new StringBuilder();

        for (User user : userList) {
            builder.append("Id: ").append(user.getId());
            builder.append("\nName: ").append(user.getName());
            builder.append(", Age: ").append(user.getAge());

            SocialAccount account = user.getSocialAccount();
            builder.append("\nS' Name: ").append(account.getName());
            builder.append(", Status: ").append(account.getStatus()).append("\n\n");
        }

        Log.d(TAG, "displayAllUsers: " + builder.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (userList != null)
            userList.removeChangeListener(userListListner);
        //userList.removeAllChangeListeners();

        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
            realmAsyncTask.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }
}
