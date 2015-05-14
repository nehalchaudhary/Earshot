package com.earshot.project.main.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.earshot.project.main.R;
import com.nineoldandroids.view.ViewHelper;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Pratik Sharma on 5/12/15.
 */
public class RecordControlFragment extends Fragment {

    View mParent;
    ImageButton mBlue;
    ImageButton mRed;
    FrameLayout mBluePair;

    final static AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    final static AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    final static DecelerateInterpolator DECELERATE = new DecelerateInterpolator();

    float startBlueX;
    float startBlueY;

    float startRedX;
    float startRedY;

    int endBlueX;
    int endBlueY;

    int startBluePairBottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.record_control_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParent = view;
        mBlue = (ImageButton) view.findViewById(R.id.transition_blue);
        mRed = (ImageButton) view.findViewById(R.id.transition_red);
        mBluePair = (FrameLayout) view.findViewById(R.id.transition_blue_pair);
        mBlue.setOnClickListener(mClicker);
    }

    View.OnClickListener mClicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startBlueX = mBlue.getX();
            startBlueY = mBlue.getY();

            endBlueX = (int) (mParent.getRight()*0.8f);
            endBlueY = (int) (mParent.getBottom()*0.8f);
//            ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(mBlue, endBlueX,
//                    endBlueY, 90, Side.LEFT)
//                    .setDuration(500);
//            arcAnimator.addListener(new SimpleListener(){
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mBlue.setVisibility(View.INVISIBLE);
//                    appearBluePair();
//                }
//            });
//            arcAnimator.start();
            mBlue.setVisibility(View.INVISIBLE);
            appearBluePair();

        }
    };

    void appearBluePair(){
        mBluePair.setVisibility(View.VISIBLE);

        float finalRadius = Math.max(mBluePair.getWidth(), mBluePair.getHeight()) * 1.5f;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mBluePair, endBlueX, endBlueY, mBlue.getWidth() / 2f,
                finalRadius);
        animator.setDuration(500);
        animator.setInterpolator(ACCELERATE);
        animator.addListener(new SimpleListener(){
            @Override
            public void onAnimationEnd() {
                raise();
            }
        });
        animator.start();
    }

    void raise(){
        startBluePairBottom = mBluePair.getBottom();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBluePair, "bottom", mBluePair.getBottom(), mBluePair.getTop() + dpToPx(100));
        objectAnimator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                upRed();
            }
        });
        objectAnimator.setInterpolator(ACCELERATE_DECELERATE);
        objectAnimator.start();
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private void upRed(){
        mRed.setVisibility(View.VISIBLE);
        startRedX = ViewHelper.getX(mRed);
        startRedY = ViewHelper.getY(mRed);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mRed, "y", ViewHelper.getY(mRed),
                mBluePair.getBottom() - mRed.getHeight() / 2);

        objectAnimator.setDuration(650);
        objectAnimator.setInterpolator(ACCELERATE_DECELERATE);
        objectAnimator.start();
    }


    private static class SimpleListener implements SupportAnimator.AnimatorListener, ObjectAnimator.AnimatorListener{

        @Override
        public void onAnimationStart() {

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

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
