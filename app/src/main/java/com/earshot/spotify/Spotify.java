package com.earshot.spotify;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.earshot.project.main.EarshotUtils;
import com.earshot.project.main.NetworkApplication;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by nehal.chaudhary on 5/3/15.
 */
public class Spotify {

    private static final String CLIENT_ID = "";
    private static final String REDIRECT_URI = "earshot://callback";
    public static final int REQUEST_CODE = 1337;
    private static final String TAG = "nlc";

    private Activity context;
    String userId;
    String playlistId;

    public Spotify(Activity context) {
        this.context = context;
    }

    public void login() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(context, REQUEST_CODE, request);
    }

    public void logout() {

        AuthenticationClient.logout(context);

        // Delete token from preferences
        EarshotUtils.storeInSharedPreference("token", null, context);

    }

    private void setUserId() {

        NetworkApplication app = (NetworkApplication)NetworkApplication.getContext();
        RequestQueue queue = app.getQueue();

        // Make a GET request to get the user id from the token
        String url = "https://api.spotify.com/v1/me";

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.i("nlc, userid returned string", response.toString());
                try {
                    JSONObject responseObject = new JSONObject(response.toString());
                    userId = responseObject.getString("id");
                    Log.i(TAG,"user id " + userId);
                } catch (JSONException e) {
                    Log.i(TAG, "tRack could not be found in spotify");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        };

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,  success, failure) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + EarshotUtils.getFromSharedPreference("token",context));
                return params;
            }
        };
        queue.add(getRequest);

    }

    private void setPlaylistId() {
        //https://api.spotify.com/v1/users/{user_id}/playlists'

        NetworkApplication app = (NetworkApplication)NetworkApplication.getContext();
        RequestQueue queue = app.getQueue();

        // Make a GET request to get the user id from the token
        String url = "https://api.spotify.com/v1/users/1299370713/playlists";

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.i("nlc, playlist returned string", response.toString());
                try {
                    JSONObject responseObject = new JSONObject(response.toString());
                    // userId = responseObject.getString("id");
                    // Log.i(TAG,"user id " + userId);
                } catch (JSONException e) {
                    Log.i(TAG, "tRack could not be found in spotify");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        };

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,  success, failure) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + EarshotUtils.getFromSharedPreference("token",context));
                return params;
            }
        };
        queue.add(getRequest);
    }

    public void addTrackToPlaylist(String trackId) {
        setUserId();
        setPlaylistId();
    }


}