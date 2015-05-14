package com.earshot.project.main.views;

//import com.nineoldandroids.view.ViewHelper;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Pratik Sharma on 5/13/15.
 */
public class Utils {
    public static float centerX(View view){
        return ViewHelper.getX(view) + view.getWidth()/2;
    }

    public static float centerY(View view){
        return ViewHelper.getY(view) + view.getHeight()/2;
    }
}
