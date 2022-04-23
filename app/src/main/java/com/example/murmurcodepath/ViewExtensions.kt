package com.example.murmurcodepath

import android.view.View
import android.view.ViewPropertyAnimator

object ViewExtensions {
    @JvmStatic
    fun fadeIn(view: View): ViewPropertyAnimator {
        view.alpha = 0.0f
        view.visibility = View.VISIBLE
        val animator = view.animate()
        animator.alpha(1.0f)
        animator.duration = 300
        return animator
    }

    @JvmStatic
    fun fadeOut(view: View): ViewPropertyAnimator {
        val animator = view.animate()
        animator.alpha(0.0f)
        animator.duration = 300
        animator.withEndAction {
            view.visibility = View.GONE
            view.alpha = 1.0f
        }
        return animator
    }
}