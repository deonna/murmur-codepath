package com.example.murmurcodepath;

import android.view.View;
import android.view.ViewPropertyAnimator;

public class ViewExtensions {

    public static ViewPropertyAnimator fadeIn(View view) {
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);

        ViewPropertyAnimator animator = view.animate();
        animator.alpha(1.0f);
        animator.setDuration(300);

        return animator;
    }

    public static ViewPropertyAnimator fadeOut(View view) {
        ViewPropertyAnimator animator = view.animate();
        animator.alpha(0.0f);
        animator.setDuration(300);
        animator.withEndAction(() -> {
            view.setVisibility(View.GONE);
            view.setAlpha(1.0f);
        });

        return animator;
    }
}