package com.app.l_pesa.common;

/**
 * Created by Intellij Amiya on 27-09-2018.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;



public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(this);
        SharedPref sharedOBJ=new SharedPref(this);

    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static MyApplication getInstance() {
        return instance;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {


    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


}
