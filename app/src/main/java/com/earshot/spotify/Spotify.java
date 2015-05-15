package com.earshot.spotify;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.earshot.project.main.EarshotUtils;
import com.earshot.project.main.NetworkApplication;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nehal.chaudhary on 5/3/15.
 */
public class Spotify {

    private static final String CLIENT_ID = "64be8836a5e9424a930ce05d6e4a697c";
    private static final String REDIRECT_URI = "earshot://callback";
    public static final int REQUEST_CODE = 1337;
    private static final String TAG = "nlc";
    private static final String PLAYLIST_NAME = "Earshot";

    private Activity context;
    String userId;
    String playlistId;

    NetworkApplication app;
    RequestQueue queue;

    public Spotify(Activity context) {
        this.context = context;
        app = (NetworkApplication)NetworkApplication.getContext();
        queue = app.getQueue();
    }

    public void login() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming","playlist-modify-public","playlist-modify-private"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(context, REQUEST_CODE, request);
    }

    public void logout() {

        AuthenticationClient.logout(context);

        // Delete token from preferences
        EarshotUtils.storeInSharedPreference("token", null, context);

    }

    public Boolean setUserId(final String trackId) {

        // If the user is not logged in
        if(EarshotUtils.getFromSharedPreference("token",context) == null)
            return false;

        // Make a GET request to get the user id from the token
        String url = "https://api.spotify.com/v1/me";

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.i("nlc, userid returned string", response.toString());
                try {
                    JSONObject responseObject = new JSONObject(response.toString());
                    userId = responseObject.getString("id");
                    setPlaylistId(trackId);
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
        return true;
    }

    public void setPlaylistId(final String trackId) {

        // Make a GET request to get the user id from the token
        String url = "https://api.spotify.com/v1/users/1299370713/playlists";

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseObject = new JSONObject(response.toString());
                    JSONArray playlistArray = responseObject.getJSONArray("items");
                    for(int i=0;i<playlistArray.length();i++) {
                        JSONObject playlist = playlistArray.getJSONObject(i);
                        if(playlist.getString("name").equals(PLAYLIST_NAME)) {
                            playlistId = playlist.getString("id");
                            addTrackToPlaylist(trackId);
                            Log.i(TAG,"PLAYLIST ID" + playlistId);
                        }
                    }
                    if(playlistId == null) {
                        Log.i(TAG,"PLAYLIST NOT FOUND");
                        createPlaylist(trackId);
                    }
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
        String url = "https://api.spotify.com/v1/users/"+userId+"/playlists/"+playlistId+"/tracks?uris=spotify%3Atrack%3A"+trackId;
        Log.i(TAG,"url"+url);
        Response.Listener<String> success = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("nlc, playlist returned string", response);
                try {
                    JSONObject responseObject = new JSONObject(response); // contains the snapshot id
                    Log.i(TAG, "song added");
                    Toast.makeText(context, "Song has been added to Earshot playlist...", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        };

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,  success, failure) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + EarshotUtils.getFromSharedPreference("token", context));
                return headers;
            }
        };
        queue.add(getRequest);
    }

    public void createPlaylist(final String trackId) {

        final String playlistInfo = "{\"name\":\"" + PLAYLIST_NAME + "\",\"public\":true}";
        // Make a POST request to get the user id from the token
        String url = "https://api.spotify.com/v1/users/"+userId+"/playlists";
        Log.i(TAG,"token" + EarshotUtils.getFromSharedPreference("token",context));

        Response.Listener<String> success = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("nlc, playlist returned string", response);
                try {
                    JSONObject responseObject = new JSONObject(response);
                    playlistId = responseObject.getString("name");
                    addTrackToPlaylist(trackId);
                    Log.i(TAG,"playlist created");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        };

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,  success, failure) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + EarshotUtils.getFromSharedPreference("token", context));
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError{
                return playlistInfo.getBytes();
            }

        };
        queue.add(getRequest);
    }


}
