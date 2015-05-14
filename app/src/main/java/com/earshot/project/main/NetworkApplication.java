package com.earshot.project.main;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nehal.chaudhary on 3/15/15.
 */
public class NetworkApplication extends Application {

    private RequestQueue queue;
    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
    }

    public void onTrimMemory(int level) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            queue.stop();
            queue = null;
        }
    }

    public RequestQueue getQueue() {
        if (queue == null)
            queue = Volley.newRequestQueue(this);
        return queue;
    }

    public static Context getContext() {
        return mContext;
    }
}
