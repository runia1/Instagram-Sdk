package com.mrunia.instagram.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by mar on 2/15/15.
 */
public class InstagramSession {
    private String accessToken, username, bio, website, fullName, id;
    private boolean active = false;

    Context context;
    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "instagramSDK";
    private static final String ACCESSTOKEN = "token";
    private static final String USERNAME = "username";
    private static final String BIO = "bio";
    private static final String WEBSITE = "website";
    private static final String FULLNAME = "fullName";
    private static final String ID = "id";

    private static InstagramSession ourInstance = new InstagramSession();
    public static InstagramSession getInstance(Context c) {
        InstagramSession session = ourInstance;
        session.context = c;
        // do this for cases where the login button is not on the first activity displayed.
        // Will restore session upon first call to InstagramSession.getInstance(context);
        if(!session.active) session.attemptSessionRestore();
        return session;
    }
    private InstagramSession() { }

    public boolean attemptSessionRestore() {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.contains(ACCESSTOKEN)) {
            //attempt to restore
            accessToken = pref.getString(ACCESSTOKEN, null);
            username = pref.getString(USERNAME, null);
            bio = pref.getString(BIO, null);
            website = pref.getString(WEBSITE, null);
            fullName = pref.getString(FULLNAME, null);
            id = pref.getString(ID, null);
            active = true;
            return true;
        } else {
            return false;
        }

    }

    public void closeSession() {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.clear();
        editor.commit();
        active = false;
    }

    public void initSession(String[] info) {
        active = true;
        accessToken = info[0];
        username = info[1];
        bio = info[2];
        website = info[3];
        fullName = info[4];
        id = info[5];
        //save this data to shared prefs.
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.putString(ACCESSTOKEN, accessToken);
        editor.putString(USERNAME, username);
        editor.putString(BIO, bio);
        editor.putString(WEBSITE, website);
        editor.putString(FULLNAME, fullName);
        editor.putString(ID, id);
        editor.commit();
    }

    public HashMap<String, String> getSessionDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        user.put(ACCESSTOKEN, pref.getString(ACCESSTOKEN, null));
        user.put(USERNAME, pref.getString(USERNAME, null));
        user.put(BIO, pref.getString(BIO, null));
        user.put(WEBSITE, pref.getString(WEBSITE, null));
        user.put(FULLNAME, pref.getString(FULLNAME, null));
        user.put(ID, pref.getString(ID, null));

        return user;
    }

    public Boolean isSessionActive() {
        return active;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }

    public String getFullname() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "\n\nInstagramSession--> \nAccessToken: "+accessToken+"\nUsername: "+username+"\nBio: "+bio+"\nWebsite: "+website+"\nFullName: "+fullName+"\nId: "+id;
    }
}
