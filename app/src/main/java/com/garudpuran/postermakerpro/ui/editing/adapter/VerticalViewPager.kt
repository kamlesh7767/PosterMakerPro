package com.garudpuran.postermakerpro.ui.editing.adapter


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

class VerticalViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    init {
        setPageTransformer(true, VerticalPageTransformer())
        overScrollMode = OVER_SCROLL_NEVER
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val intercepted = super.onInterceptTouchEvent(swapXY(ev))
        swapXY(ev)
        return intercepted
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(swapXY(ev))
    }

    private fun swapXY(ev: MotionEvent): MotionEvent {
        val newX = (ev.y / height) * width
        val newY = (ev.x / width) * height
        ev.setLocation(newX, newY)
        return ev
    }

    private inner class VerticalPageTransformer : PageTransformer {
        override fun transformPage(view: View, position: Float) {
            if (position < -1 || position > 1) {
                view.alpha = 0f
            } else {
                view.alpha = 1f
                view.translationX = view.width * -position
                view.translationY = position * view.height
            }
        }
    }
}
