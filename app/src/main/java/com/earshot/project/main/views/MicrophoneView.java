package com.earshot.project.main.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.earshot.project.main.R;

/**
 * Created by Pratik Sharma on 5/12/15.
 */
public class MicrophoneView extends View  {

    private static final int OUTER = Color.parseColor("#bebebe");
    private static final int MICROPHONE_CIRCLE_RADIUS = 70;

    private Paint innerCirclePaint;
    private Paint outerCirclePaint;
    private Paint audioLevelCirclePaint;
    private Paint bitmapPaint;


    private Bitmap buttonBitmap;
    private Bitmap buttonLightBitmap;
    private boolean isRecording;

    public MicrophoneView(Context context) {
        super(context);
        init();
    }

    public MicrophoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MicrophoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public boolean toggle(){
        if (isRecording)
            close();
        else
            start();
        return isRecording;
    }

    private void start(){
        isRecording = true;
        invalidate();
    }

    private void close(){
        isRecording = false;
        invalidate();
    }

    private void init() {
        buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_mic_out_grey);
        buttonLightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_mic_out_light);
        isRecording = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (innerCirclePaint == null) {
            // Inner circle with mic
            innerCirclePaint = new Paint();
            if (!isRecording)
                innerCirclePaint.setColor(Color.WHITE);
            if (isRecording)
                innerCirclePaint.setColor(Color.RED);
            innerCirclePaint.setStrokeWidth(1);
            innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            innerCirclePaint.setAntiAlias(true);

            // Circle around inner circle
            outerCirclePaint = new Paint();
            outerCirclePaint.setColor(OUTER);
            outerCirclePaint.setStrokeWidth(5);
            outerCirclePaint.setStyle(Paint.Style.STROKE);
            outerCirclePaint.setAntiAlias(true);

            audioLevelCirclePaint = new Paint();
            audioLevelCirclePaint.setColor(OUTER);
            audioLevelCirclePaint.setStrokeWidth(1);
            audioLevelCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            audioLevelCirclePaint.setAntiAlias(true);

            bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(true);
            bitmapPaint.setFilterBitmap(true);
            bitmapPaint.setDither(true);
        }

        Bitmap bitmap = null;

        if (isRecording) {
            bitmap = buttonLightBitmap;
            innerCirclePaint.setColor(Color.parseColor("#f44336"));
        }
        else {
            bitmap = buttonBitmap;
            innerCirclePaint.setColor(Color.WHITE);
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, MICROPHONE_CIRCLE_RADIUS, innerCirclePaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, MICROPHONE_CIRCLE_RADIUS + 5, outerCirclePaint);

        if (bitmap != null) {
			// Draw center microphone
            canvas.drawBitmap(bitmap,
                    (getWidth() / 2) - (buttonBitmap.getWidth() / 2),
                    (getHeight() / 2) - (buttonBitmap.getHeight() / 2),
                    bitmapPaint);
        }
    }


    public void setRecordingMode(boolean recording) {
        isRecording = recording;
        invalidate();
    }

    public boolean getRecordingMode(){
        return isRecording;
    }


}
