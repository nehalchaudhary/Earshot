package com.earshot.doreso.record;

import android.app.Activity;
import android.util.Log;

import com.doreso.sdk.DoresoConfig;
import com.doreso.sdk.DoresoListener;
import com.doreso.sdk.DoresoManager;
import com.doreso.sdk.utils.DoresoMusicTrack;
import com.doreso.sdk.utils.DoresoUtils;

/**
 * Created by nehal.chaudhary on 4/27/15.
 */
public class RecordListener implements DoresoRecordInterface,DoresoListener {

    public interface RecordListnerResultListener {
        void onRecognizeSuccess(String artist, String track);
        void onRecognozeFailure();
    }

    private DoresoManager mDoresoManager;
    private DoresoRecord mDoresoRecord;
    private boolean mProcessing;
    private DoresoConfig mConfig;
    private static final String TAG = RecordListener.class.getSimpleName();
    private static final String APPKEY = "SZvaZLlThD5N4zOwiuWEvKCXjOAs2cDxzJFDFPeXg1k";
    private static final String APPSECRET = "ae08fc357670a7ecbefdbffb9f176c10";
    RecordListnerResultListener listener;

    public RecordListener(Activity context) {
        mConfig = new DoresoConfig();
        mConfig.appkey = APPKEY;
        mConfig.appSecret = APPSECRET;
        mConfig.listener = this;
        mConfig.context = context;
        mDoresoManager = new DoresoManager(mConfig);
        mDoresoRecord = new DoresoRecord(this, 16 * 1000);
    }

    public void registerListener(RecordListnerResultListener listener){
        this.listener = listener;
    }

    public void start() {
        if (!mProcessing) {
            mProcessing = true;
            if (mDoresoRecord!=null) {
                mDoresoRecord.reqCancel();
                mDoresoRecord = null;
            }
            mDoresoRecord = new DoresoRecord(this, 15*1024);
            mDoresoRecord.start();
            if (!mDoresoManager.startRecognize()) {
//                      Toast.makeText(, "无网络,无法识别", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void stop() {
        if (mProcessing)
            mDoresoRecord.reqStop();
    }

    public void cancel() {
        if (mProcessing) {
            mDoresoManager.cancel();
            mProcessing = false;
        }
    }

    @Override
    public void onRecording(byte[] buffer) {
        double volume = DoresoUtils.computeDb(buffer, buffer.length);
        Log.i("nlc", "volume" + volume);
        mDoresoManager.doRecognize(buffer);
    }

    @Override
    public void onRecordEnd() {
        mDoresoManager.stopRecognize();
        mProcessing = false;
    }

    @Override
    public void onRecognizeSuccess(DoresoMusicTrack[] tracks, String result) {
        // TODO Auto-generated method stub
        mDoresoManager.stopRecognize();

        Log.i(TAG, tracks.length + "\n"
                + "artist" + "\n"
                + tracks[0].getArtist() + "\n"
                + "title" + "\n"
                + tracks[0].getName() + "\n"
                + "album" + "\n"
                + tracks[0].getAlbum() + "\n" + result);

        mProcessing = false;
        listener.onRecognizeSuccess(tracks[0].getArtist(), tracks[0].getName());

    }

    @Override
    public void onRecognizeFail(int errorcode, String msg) {
        mDoresoManager.cancel();

        Log.i(TAG,errorcode + ":" + msg);
        mProcessing = false;
        listener.onRecognozeFailure();
    }

    @Override
    public void onRecognizeEnd() {
        Log.e(TAG, "onRecognizeEnd");
        mProcessing = false;
        mDoresoRecord.reqCancel();
    }

    @Override
    public void onRecordError(int errorcode, String msg) {
        Log.e(TAG, "onRecordError:" + msg + "//" + errorcode);
    }
}