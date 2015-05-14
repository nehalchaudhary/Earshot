package com.earshot.project.main;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nehal.chaudhary on 5/13/15.
 */
public class EarshotUtils {

    public static void storeInSharedPreference(String key, String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getFromSharedPreference(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

}
