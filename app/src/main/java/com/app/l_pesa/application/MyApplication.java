package com.app.l_pesa.application;

import android.app.Application;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.app.l_pesa.analytics.AnalyticsTrackers;
import com.app.l_pesa.common.SharedPref;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MyApplication extends MultiDexApplication {

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();


        //--------OTP Hash code creation
       // AppSignatureHelper appSignature = new AppSignatureHelper(this); //Should be removed in production
       // appSignature.getAppSignatures();
        //------------------------------
        mInstance = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mInstance);
        if(new SharedPref(mInstance).isDarkTheme()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        /*FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);



    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    private synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    public void getGoogleAnalyticsLogger(String method,int type){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);

        if(type ==0) {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
        }else {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
        }
    }

    public void getGoogleAnalyticsLogger(String contentId,String contentType){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT, contentId);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
}

