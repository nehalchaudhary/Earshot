package com.earshot.spotify;

/**
 * Created by nehal.chaudhary on 5/3/15.
 */
public class SpotifyTrack {

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public SpotifyTrack(String track, String trackId, String artist, String album, String previewURL, String popularity, String uri, String imageURL) {
        this.track = track;
        this.trackId = trackId;
        this.artist = artist;
        this.album = album;
        this.previewURL = previewURL;
        this.popularity = popularity;
        this.uri = uri;
        this.imageURL = imageURL;
    }

    private String track;
    private String trackId;
    private String artist;
    private String album;
    private String previewURL;
    private String popularity;
    private String uri;
    private String imageURL;


    public String getUri() {
        return uri;
    }

//    public SpotifyTrack(String trackName, String artistName, Activity context) {
        // Get the request queue for the application
        //Context context = NetworkApplication.getContext();
//        NetworkApplication app = (NetworkApplication)NetworkApplication.getContext();
//        RequestQueue queue = app.getQueue();
//
//        trackName = trackName.replaceAll(" ","%20");
//
//        trackName = trackName.replaceAll("\\(.*\\)",""); // In case the name contains (new album)
//        Log.i(TAG,"track "+trackName);
//
//        artistName = artistName.replaceAll(" ","%20");
//        artistName = artistName.replaceAll("[^a-zA-Z0-9%20]","*"); // Remove any special characters like ! or ^
//        Log.i(TAG,"artist "+artistName);
//
//        // Make a GET request to get the information about the instructor
//        String url = "https://api.spotify.com/v1/search?"
//                + "q=track:" + trackName
//                + "+artist:" + artistName
//
//                + "&type=track&limit=1";

//        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
//            public void onResponse(JSONObject response) {
//                Log.i("nlc, spotify returned string", response.toString());
//                try {
//                    JSONObject responseObject = new JSONObject(response.toString());
//                    JSONObject tracksObject = responseObject.getJSONObject("tracks");
//                    JSONArray tracksArray = tracksObject.getJSONArray("items");
//
//                    JSONObject trackObject = tracksArray.getJSONObject(0);
//                    previewURL = trackObject.getString("preview_url");
//                    uri = trackObject.getString("uri");
//                    trackId = trackObject.getString("id");
//                    popularity = trackObject.getString("popularity");
//                    track = trackObject.getString("name");
//
//                    JSONObject albumObject = trackObject.getJSONObject("album");
//                    album = albumObject.getString("name");
//
//                    JSONArray imagesArray = albumObject.getJSONArray("images");
//                    imageURL = imagesArray.getJSONObject(0).getString("url");
//
//                    JSONArray artistsArray = trackObject.getJSONArray("artists");
//                    JSONObject artistObject = artistsArray.getJSONObject(0);
//                    artist = artistObject.getString("name");
//
//
//                    Log.i(TAG,"previewurl "+previewURL);
//                    Log.i(TAG,"uri "+ uri);
//                    Log.i(TAG,"trackid "+ trackId);
//                    Log.i(TAG,"popularity "+ popularity);
//                    Log.i(TAG,"track "+ track);
//                    Log.i(TAG,"album "+ album);
//                    Log.i(TAG,"artist "+ artist);
//                    Log.i(TAG,"image "+ imageURL);
//
//                } catch (JSONException e) {
//                    Log.i(TAG,"tRack could not be found in spotify");
//                    e.printStackTrace();
//                }
//            }
//        };
//        Response.ErrorListener failure = new Response.ErrorListener() {
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, error.toString());
//            }
//        };

//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,  success, failure);
//        queue.add(getRequest);
//    }

}