package com.garudpuran.postermakerpro.ui.dashboard

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class CustomNestedScrollView(context: Context, attrs: AttributeSet?) : NestedScrollView(context, attrs) {
    private var savedScrollState: Parcelable? = null

    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val customState = SavedState(superState)
        customState.savedScrollState = savedScrollState
        return customState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            savedScrollState = state.savedScrollState
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setSavedScrollState(scrollState: Parcelable?) {
        savedScrollState = scrollState
    }

    private class SavedState : BaseSavedState {
        var savedScrollState: Parcelable? = null

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            savedScrollState = parcel.readParcelable(SavedState::class.java.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeParcelable(savedScrollState, flags)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
