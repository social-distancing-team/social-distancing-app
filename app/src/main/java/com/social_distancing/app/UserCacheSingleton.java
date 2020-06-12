package com.social_distancing.app;

import android.util.Log;

public class UserCacheSingleton {
    private static UserCache mInstance;
    private UserCache userCache = null;

    private UserCacheSingleton() {
        userCache = new UserCache();
    }

    public static synchronized UserCacheSingleton getInstance() {
        if (mInstance == null){
            mInstance = new UserCacheSingleton();
        }
        return mInstance;
    }
}
