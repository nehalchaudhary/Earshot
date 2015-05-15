package com.earshot.project.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.earshot.doreso.record.RecordListener;
import com.earshot.project.main.views.AnimationState;
import com.earshot.project.main.views.MicrophoneView;
import com.earshot.project.main.views.Ripples;
import com.earshot.spotify.Spotify;
import com.earshot.spotify.SpotifySingleton;
import com.earshot.spotify.SpotifyTrack;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * doresosdk demo
 * 
 * @author jzx
 */
public class MainActivity extends FragmentActivity implements RecordListener.RecordListnerResultListener {

    RecordListener mRecordListener;
    Spotify mSpotify;
    protected Button mLoginButton;
    private TextView mUserName;
    String token;


    private static final String TAG = MainActivity.class.getSimpleName();


    private MicrophoneView mRecordButton;

    protected RelativeLayout mWrapper;
    protected FrameLayout mToolbar;
    protected RelativeLayout mToolbarSong;
    protected LinearLayout mSongDetails;
    protected TextView mTextViewSongName;
    protected TextView mTextViewAlbumName;
    protected TextView mTextViewArtistName;
    protected View mButtonAdd;
    protected View mOverlay;
    protected Ripples mRippleBackground;

    private float mInitialSongButtonX;


    private AnimatorSet mOpenSongAnimatorSet;
    private AnimatorSet mCloseSongAnimatorSet;
    private Animation mAddButtonShowAnimation;

    private AnimationState mState = AnimationState.Closed;

    static int sScreenWidth;
    static int sSongImageHeight;

    protected SpotifyTrack spotifyTrack;
    protected Spotify spotify;


    //Animation constants
    private static final int MAX_DELAY_SHOW_DETAILS_ANIMATION = 500;
    private static final int ANIMATION_DURATION_SHOW_ADD_BUTTON = 300;
    private static final int ANIMATION_DURATION_SHOW_SONG_DETAILS = 500;
    private static final int STEP_DELAY_HIDE_DETAILS_ANIMATION = 80;
    private static final int ANIMATION_DURATION_CLOSE_SONG_DETAILS = 500;
    private static final int CIRCLE_RADIUS_DP = 50;
    private static final int REVEAL_ANIMATION_DURATION = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWrapper = (RelativeLayout) findViewById(R.id.wrapper);
        mToolbar = (FrameLayout) findViewById(R.id.main_toolbar);
        mToolbarSong = (RelativeLayout) findViewById(R.id.song_details_toolbar);
        mSongDetails = (LinearLayout) findViewById(R.id.song_details);
        mTextViewSongName = (TextView) findViewById(R.id.text_view_song_name);
        mTextViewAlbumName = (TextView) findViewById(R.id.text_view_album_name);
        mTextViewArtistName = (TextView) findViewById(R.id.text_view_artist_name);
        mButtonAdd = findViewById(R.id.spotify_add);

        mRippleBackground =(Ripples)findViewById(R.id.ripples);

        mRecordButton = (MicrophoneView) findViewById(R.id.micButton);
        mLoginButton = (Button) findViewById(R.id.loginButton);

        mInitialSongButtonX = mButtonAdd.getX();
        spotify = SpotifySingleton.getSpotifyInstance(this);

        findViewById(R.id.song_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCloseSongDetails();
            }
        });
        sScreenWidth = getResources().getDisplayMetrics().widthPixels;
        sSongImageHeight = getResources().getDimensionPixelSize(R.dimen.height_song_image);
    }


    @Override
    protected void onResume(){
        super.onResume();
        mRecordListener = new RecordListener(this);
        mRecordListener.registerListener(this);
        mSpotify = SpotifySingleton.getSpotifyInstance(this);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotify.login();
            }
        });

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordButton.toggle()){
                    mRippleBackground.startRippleAnimation();
                    mRecordListener.start();
                } else {
                    mRippleBackground.stopRippleAnimation();
                    mRecordListener.stop();
                }
            }
        });

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addToPlyalist();
            }
        });
    }


    public void addToPlyalist(){
        String token = EarshotUtils.getFromSharedPreference("token",getApplicationContext());
        if (token == null)
            spotify.login();
        else
            spotify.setUserId(spotifyTrack.getTrackId());
    }

    private void showSongDetails(SpotifyTrack track){
        mRippleBackground.stopRippleAnimation();
        mRippleBackground.setVisibility(View.INVISIBLE);
        mRecordButton.setRecordingMode(false);
        mRecordButton.setVisibility(View.INVISIBLE);

        //if we add more stuff at radom places, this will keep the time normalized based
        //on the position where the click happened!

        int songDetailsAnimationDelay = MAX_DELAY_SHOW_DETAILS_ANIMATION * Math.abs(mRecordButton.getTop())
                / sScreenWidth;

        //Make copy
        makeSongImageDetailsView(track);
        //responsible for the circular reveal
        startRevealAnimation(songDetailsAnimationDelay);
        //other transitions to put data where it belongs
        animateOpenSongDetails(songDetailsAnimationDelay);
    }


    private void makeSongImageDetailsView(SpotifyTrack track){
        if (mOverlay == null)
            mOverlay = getLayoutInflater().inflate(R.layout.overlay_song, mWrapper, false);
        else
            mWrapper.removeView(mOverlay);

        Picasso.with(MainActivity.this).load(track.getImageURL())
                .resize(sScreenWidth, sSongImageHeight).centerCrop()
                .placeholder(R.color.indigo)
                .into((ImageView) mOverlay.findViewById(R.id.image_view_reveal_avatar));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDisplayMetrics().heightPixels;
        params.bottomMargin = getResources().getDisplayMetrics().widthPixels;
        mWrapper.addView(mOverlay, params);

        mTextViewSongName.setText(track.getTrack());
        mTextViewAlbumName.setText(track.getAlbum());
        mTextViewArtistName.setText(track.getArtist());
        mToolbar.bringToFront();

    }


    private void startRevealAnimation(final int songDetailsAnimationDelay) {
        mOverlay.post(new Runnable() {
            @Override
            public void run() {
                getAvatarRevealAnimator().start();
                getAvatarShowAnimator(songDetailsAnimationDelay).start();
            }
        });
    }

    private SupportAnimator getAvatarRevealAnimator() {
        final LinearLayout mWrapperListItemReveal = (LinearLayout) mOverlay.findViewById(R.id.wrapper_song_reveal);

        int finalRadius = Math.max(mOverlay.getWidth(), mOverlay.getHeight());

        final SupportAnimator mRevealAnimator = ViewAnimationUtils.createCircularReveal(
                mWrapperListItemReveal,
                sScreenWidth / 2,
                sSongImageHeight / 2,
                dpToPx(CIRCLE_RADIUS_DP * 2),
                finalRadius);
        mRevealAnimator.setDuration(REVEAL_ANIMATION_DURATION);
        mRevealAnimator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                mWrapperListItemReveal.setVisibility(View.VISIBLE);
                mOverlay.setX(0);
            }

            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        return mRevealAnimator;
    }

    private Animator getAvatarShowAnimator(int songDetailsAnimationDelay) {
        final Animator mAvatarShowAnimator = ObjectAnimator.ofFloat(mOverlay, View.Y, mOverlay.getTop(), mToolbarSong.getBottom());
        mAvatarShowAnimator.setDuration(songDetailsAnimationDelay + ANIMATION_DURATION_CLOSE_SONG_DETAILS);
        mAvatarShowAnimator.setInterpolator(new DecelerateInterpolator());
        return mAvatarShowAnimator;
    }

    private void animateOpenSongDetails(int songDetailsAnimationDelay) {
        createOpenSongButtonAnimation();
        getOpenSongAnimatorSet(songDetailsAnimationDelay).start();
    }

    private void createOpenSongButtonAnimation() {
        if (mAddButtonShowAnimation == null) {
            mAddButtonShowAnimation = AnimationUtils.loadAnimation(this, R.anim.add_button_scale);
            mAddButtonShowAnimation.setDuration(ANIMATION_DURATION_SHOW_ADD_BUTTON);
            mAddButtonShowAnimation.setInterpolator(new AccelerateInterpolator());
            mAddButtonShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mButtonAdd.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private AnimatorSet getOpenSongAnimatorSet(int songDetailsAnimationDelay) {
        if (mOpenSongAnimatorSet == null) {
            List<Animator> songAnimators = new ArrayList<>();
            songAnimators.add(getOpenSongToolbarAnimator());
            songAnimators.add(getOpenSongDetailsAnimator());

            mOpenSongAnimatorSet = new AnimatorSet();
            mOpenSongAnimatorSet.playTogether(songAnimators);
            mOpenSongAnimatorSet.setDuration(ANIMATION_DURATION_SHOW_SONG_DETAILS);
        }
        mOpenSongAnimatorSet.setStartDelay(songDetailsAnimationDelay);
        mOpenSongAnimatorSet.setInterpolator(new DecelerateInterpolator());
        return mOpenSongAnimatorSet;
    }


    private Animator getOpenSongToolbarAnimator() {
        Animator mOpenSongToolbarAnimator = ObjectAnimator.ofFloat(mToolbarSong, View.Y, -mToolbarSong.getHeight(), 0);
        mOpenSongToolbarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mToolbarSong.setX(0);
                mToolbarSong.bringToFront();
                mToolbarSong.setVisibility(View.VISIBLE);
                mSongDetails.setX(0);
                mSongDetails.bringToFront();
                mSongDetails.setVisibility(View.VISIBLE);
                mButtonAdd.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mButtonAdd.startAnimation(mAddButtonShowAnimation);

                mState = AnimationState.Opened;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return mOpenSongToolbarAnimator;
    }

    private Animator getOpenSongDetailsAnimator() {
        Animator mOpenSongDetailsAnimator = ObjectAnimator.ofFloat(mSongDetails, View.Y,
                getResources().getDisplayMetrics().heightPixels,
                getResources().getDimensionPixelSize(R.dimen.height_song_picture_with_toolbar));
        return mOpenSongDetailsAnimator;
    }


    private void animateCloseSongDetails() {
        mState = AnimationState.Closing;
        getCloseSongAnimatorSet().start();
    }


    private AnimatorSet getCloseSongAnimatorSet() {
        if (mCloseSongAnimatorSet == null) {
            Animator songToolbarAnimator = ObjectAnimator.ofFloat(mToolbarSong, View.X,
                    0, mToolbarSong.getWidth());

            Animator songPhotoAnimator = ObjectAnimator.ofFloat(mOverlay, View.X,
                    0, mOverlay.getWidth());
            songPhotoAnimator.setStartDelay(STEP_DELAY_HIDE_DETAILS_ANIMATION);

            Animator songDetailsAnimator = ObjectAnimator.ofFloat(mSongDetails, View.X,
                    0, mToolbarSong.getWidth());
            songDetailsAnimator.setStartDelay(STEP_DELAY_HIDE_DETAILS_ANIMATION * 2);

            List<Animator> songAnimators = new ArrayList<>();
            songAnimators.add(songToolbarAnimator);
            songAnimators.add(songPhotoAnimator);
            songAnimators.add(songDetailsAnimator);

            mCloseSongAnimatorSet = new AnimatorSet();
            mCloseSongAnimatorSet.playTogether(songAnimators);
            mCloseSongAnimatorSet.setDuration(ANIMATION_DURATION_CLOSE_SONG_DETAILS);
            mCloseSongAnimatorSet.setInterpolator(new AccelerateInterpolator());
            mCloseSongAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mButtonAdd.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mToolbarSong.setVisibility(View.INVISIBLE);
                    mSongDetails.setVisibility(View.INVISIBLE);
                    mRecordButton.setVisibility(View.VISIBLE);
                    mRippleBackground.setVisibility(View.VISIBLE);
                    mState = AnimationState.Closed;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return mCloseSongAnimatorSet;
    }

    public int dpToPx(int dp) {
        return Math.round((float) dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onRecognizeSuccess(String artist, String track) {
        getDataFromSpotify(artist, track);

        mState = AnimationState.Opening;

    }

    public void getDataFromSpotify(String artist, String track){

        NetworkApplication app = (NetworkApplication) getApplicationContext();
        RequestQueue queue = app.getQueue();
        track = track.replaceAll(" ","%20");
        track = track.replaceAll("\\(.*\\)",""); // In case the name contains (new album)

        artist = artist.replaceAll(" ","%20");
        artist = artist.replaceAll("[^a-zA-Z0-9%20]","*"); // Remove any special characters like ! or ^

        // Make a GET request to get the information about the instructor
        String url = "https://api.spotify.com/v1/search?"
                + "q=track:" + track
                + "+artist:" + artist
                + "&type=track&limit=1";



        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,  successSong, failureSong);
        queue.add(getRequest);
    }

    Response.Listener<JSONObject> successSong = new Response.Listener<JSONObject>() {
        public void onResponse(JSONObject response) {

            try {
                JSONObject responseObject = new JSONObject(response.toString());
                JSONObject tracksObject = responseObject.getJSONObject("tracks");
                JSONArray tracksArray = tracksObject.getJSONArray("items");

                JSONObject trackObject = tracksArray.getJSONObject(0);
                String previewURL = trackObject.getString("preview_url");
                String uri = trackObject.getString("uri");
                String trackId = trackObject.getString("id");
                String popularity = trackObject.getString("popularity");
                String track = trackObject.getString("name");

                JSONObject albumObject = trackObject.getJSONObject("album");
                String album = albumObject.getString("name");

                JSONArray imagesArray = albumObject.getJSONArray("images");
                String imageURL = imagesArray.getJSONObject(0).getString("url");

                JSONArray artistsArray = trackObject.getJSONArray("artists");
                JSONObject artistObject = artistsArray.getJSONObject(0);
                String artist = artistObject.getString("name");

                spotifyTrack = new SpotifyTrack(track, trackId, artist, album, previewURL, popularity, uri, imageURL);
                showSongDetails(spotifyTrack);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Couldn't find the track on Spotify", Toast.LENGTH_LONG).show();
                if (mRecordButton.getRecordingMode()){
                    mRippleBackground.stopRippleAnimation();
                    mRecordButton.setRecordingMode(false);
                    mRecordButton.invalidate();
                }
                Log.i(TAG,"tRack could not be found in spotify");
                e.printStackTrace();
            }
        }
    };


    Response.ErrorListener failureSong = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, error.toString());
        }
    };




    @Override
    public void onRecognozeFailure() {
        if (mRecordButton.getRecordingMode()){
            mRippleBackground.stopRippleAnimation();
            mRecordButton.setRecordingMode(false);
            mRecordButton.invalidate();
        }
        Toast.makeText(getApplicationContext(), "Couldn't recognize the track.", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == Spotify.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Save the token in shared preferences
                    EarshotUtils.storeInSharedPreference("token", response.getAccessToken(),this);
                    spotify.setUserId(spotifyTrack.getTrackId());
                    Log.i(TAG, "AUTHENTICATION SUCCESSFUL");
                    break;

                case ERROR:
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
}
