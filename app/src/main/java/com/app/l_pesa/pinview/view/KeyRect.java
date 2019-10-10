package com.app.l_pesa.pinview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;


class KeyRect {

    private final View view;
    public Rect rect;
    public String value;
    public int rippleRadius = 0;
    private int requiredRadius;
    public int circleAlpha;
    //private final Rect tempRect;
    public boolean hasRippleEffect = false;
    private ValueAnimator animator;
    private final int MAX_RIPPLE_ALPHA = 180;
    private InterpolatedValueListener interpolatedValueListener;
    private int animationLeftRepeatCount = 2;
    private int animationRightRepeatCount = 2;
    private int cycleCount = 4;
    private RippleAnimListener rippleAnimListener;

     KeyRect(View view, Rect rect, String value) {
        this.view = view;
        this.rect = rect;
        //this.tempRect = new Rect(rect);
        this.value = value;
        requiredRadius = (this.rect.right - this.rect.left) / 4;
        setUpAnimator();
    }

    /**
     * Initialise the filed and listener for ripple effect animator
     */
    private void setUpAnimator() {
        animator = ValueAnimator.ofFloat(0, requiredRadius);
        animator.setDuration(400);
        final int circleAlphaOffset = MAX_RIPPLE_ALPHA / requiredRadius;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                if (hasRippleEffect) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    rippleRadius = (int) animatedValue;
                    Log.i("Ripple start", "radius " + rippleRadius);
                    circleAlpha = (int) (MAX_RIPPLE_ALPHA - (animatedValue * circleAlphaOffset));
                    interpolatedValueListener.onValueUpdated();
                }
            }
        });

        animator.addListener(new ValueAnimator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
                hasRippleEffect = true;
            }

            @Override public void onAnimationEnd(Animator animation) {
                hasRippleEffect = false;
                rippleRadius = 0;
                rippleAnimListener.onEnd();
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * Set the valued of this Key
     *
     * @param value - Value to be set for this key
     */
    void setValue(String value) {
        this.value = value;
    }

    /**
     * Show animation indicated invalid pincode
     */
    void setError() {
        ValueAnimator goLeftAnimator = ValueAnimator.ofInt(0, 5);
        goLeftAnimator.setInterpolator(new CycleInterpolator(2));
        goLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                rect.left += (int) animation.getAnimatedValue();
                rect.right += (int) animation.getAnimatedValue();
                view.invalidate();
            }
        });
        goLeftAnimator.start();
    }

    public interface InterpolatedValueListener {
        void onValueUpdated();
    }

    private void setOnValueUpdateListener(InterpolatedValueListener listener) {
        this.interpolatedValueListener = listener;
    }


     void playRippleAnim(RippleAnimListener listener) {
        this.rippleAnimListener = listener;
        setOnValueUpdateListener(new KeyRect.InterpolatedValueListener() {
            @Override public void onValueUpdated() {
                //view.invalidate(rect);
                view.invalidate();
            }
        });
        rippleAnimListener.onStart();
        animator.start();
    }


    /**
     * Interface to get notified on ripple animation status
     */
    public interface RippleAnimListener {
        void onStart();

        void onEnd();
    }
}