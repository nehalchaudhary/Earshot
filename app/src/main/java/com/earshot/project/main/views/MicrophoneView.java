package com.earshot.project.main.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.earshot.project.main.R;

/**
 * Created by Pratik Sharma on 5/12/15.
 */
public class MicrophoneView extends View  {


    //    TODO: get values from xml
    private static final int INNER = Color.parseColor("#ff4444");
    private static final int OUTER = Color.parseColor("#bebebe");
    private static final int MICROPHONE_CIRCLE_RADIUS = 100;

    private int touchRadius = MICROPHONE_CIRCLE_RADIUS;
    private double audioLevel = 0;

    private Paint innerCirclePaint;
    private Paint outerCirclePaint;
    private Paint audioLevelCirclePaint;
    private Paint bitmapPaint;


    private Bitmap buttonBitmap;
    private Bitmap buttonLightBitmap;
    private Bitmap buttonPressedBitmap;

    private boolean isSelected;
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

    private void init() {
        buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_mic_out_grey);
        buttonLightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_mic_out_light);
        buttonPressedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_mic_out_pressed);
        isRecording = false;
        isSelected = false;
    }

    private static boolean isInCircle(float x, float y, float circleCenterX, float circleCenterY, float circleRadius) {
        double dx = Math.pow(x - circleCenterX, 2);
        double dy = Math.pow(y - circleCenterY, 2);

        if ((dx + dy) < Math.pow(circleRadius, 2)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean shouldReturnFalse = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInCircle(event.getX(), event.getY(), getWidth() / 2, getHeight() / 2, touchRadius)) {
                innerCirclePaint.setColor(OUTER);
                isSelected = true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isInCircle(event.getX(), event.getY(), getWidth() / 2, getHeight() / 2, touchRadius)) {
                innerCirclePaint.setColor(OUTER);
                isSelected = true;
            } else {
                if (!isRecording)
                    innerCirclePaint.setColor(Color.WHITE);
                else
                    innerCirclePaint.setColor(INNER);
                shouldReturnFalse = true;
                isSelected = false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isInCircle(event.getX(), event.getY(), getWidth() / 2, getHeight() / 2, touchRadius)) {
                isSelected = false;
            } else {
                if (!isRecording)
                    innerCirclePaint.setColor(Color.WHITE);
                else
                    innerCirclePaint.setColor(INNER);
                shouldReturnFalse = true;
                isSelected = false;
            }
        }
        invalidate();
        if (shouldReturnFalse)
            return false;

        return super.onTouchEvent(event);
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
                innerCirclePaint.setColor(INNER);
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

        if ((audioLevel > 0) && isRecording) {
            double radius = audioLevel * (getWidth() / 3) / 100;
            if (radius > getWidth() / 3)
                radius = getWidth() / 3;
            touchRadius = (int) radius;
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, (int) radius, audioLevelCirclePaint);
        } else {
            touchRadius = MICROPHONE_CIRCLE_RADIUS;
        }

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, MICROPHONE_CIRCLE_RADIUS, innerCirclePaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, MICROPHONE_CIRCLE_RADIUS + 5, outerCirclePaint);

        Bitmap bitmap = null;

        if (isRecording)
            bitmap = buttonLightBitmap;
        else if (!isRecording)
            bitmap = buttonBitmap;

        if (isSelected) {
            bitmap = buttonPressedBitmap;
        }

        if (bitmap != null) {
			/* Draw center microphone */
            canvas.drawBitmap(bitmap,
                    (getWidth() / 2) - (buttonBitmap.getWidth() / 2),
                    (getHeight() / 2) - (buttonBitmap.getHeight() / 2),
                    bitmapPaint);
        }
    }

    private boolean changingValue = false;


    public void setRecordingMode(boolean recording) {
        isRecording = recording;
        if (innerCirclePaint == null)
            return;

        if (!isRecording)
            innerCirclePaint.setColor(Color.WHITE);
        else if (isRecording)
            innerCirclePaint.setColor(INNER);

        invalidate();
    }

//    @Override
//    public void onAudioLevelChanged(double percentage) {
//        percentage *= 50;
//        if (this.audioLevel == percentage)
//            return;
//
//        if (!isRecording) {
//            isRecording = true;
//        }
//
//        if (changingValue)
//            return;
//
//        changingValue = true;
//
//        int diff = (int) Math.abs(this.audioLevel - percentage);
//
////        while (diff > 0) {
////            if (this.audioLevel > percentage) {
////                audioLevel-=10;
////            } else {
////                audioLevel+=10;
////            }
////
////            diff = (int) Math.abs(this.audioLevel - percentage);
////            invalidate();
////        }
//
//        changingValue = false;
//
//        this.audioLevel = percentage;
//        invalidate();
//
//    }
}
