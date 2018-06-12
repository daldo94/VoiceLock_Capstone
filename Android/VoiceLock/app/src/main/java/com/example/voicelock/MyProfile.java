package com.example.voicelock;

/**
 * Created by DohyunKim on 2018-05-25.
 */

public class MyProfile {
    private String profile;

    public String getProfile(){
        return profile;
    }

    public void setProfile(String data){
        this.profile=data;
    }

    private static MyProfile instance = null;

    public static synchronized MyProfile getInstance(){
        if(null==instance){
            instance = new MyProfile();
        }
        return instance;
    }
}
