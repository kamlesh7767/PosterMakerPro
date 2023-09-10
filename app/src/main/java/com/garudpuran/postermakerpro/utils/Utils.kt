package com.garudpuran.postermakerpro.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.garudpuran.postermakerpro.R.id
import com.garudpuran.postermakerpro.R.layout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object Utils {

    fun showToast(activity: Activity, message: String){
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_SHORT
      toast.setGravity(Gravity.BOTTOM,0,0)
        val inflater = activity.layoutInflater

        val view: View = inflater.inflate(
            layout.layout_toast, activity.findViewById(id.toast_constraint_layout))
        val toastTxt: TextView =view.findViewById(id.toast_txt)
        toastTxt.text =  message
        toast.view = view
        toast.show()
    }

    fun getProfileBottomPopUpStatus(activity: Activity):Boolean{
        val profilePref = activity.getSharedPreferences(
           UserReferences.USER_PROFILE,
            Context.MODE_PRIVATE
        )
        val pref = profilePref.getString(UserReferences.USER_PROFILE_STATUS,"")
        return pref != UserReferences.USER_PROFILE_STATUS_SHOWED
    }

    fun getReviewPopUpStatus(activity: Activity):Boolean{
        val profilePref = activity.getSharedPreferences(
            UserReferences.USER_PROFILE,
            Context.MODE_PRIVATE
        )
        val pref = profilePref.getString(UserReferences.USER_REVIEW_STATUS_SHOW,UserReferences.USER_REVIEW_STATUS_DO_SHOW)
        return pref == UserReferences.USER_REVIEW_STATUS_DO_SHOW
    }

    fun getIntroStatus(activity: Activity):Boolean{
        val profilePref = activity.getSharedPreferences(
            UserReferences.USER_INTRO,
            Context.MODE_PRIVATE
        )
        val pref = profilePref.getString(UserReferences.USER_INTRO_STATUS,"")
        return pref != UserReferences.USER_INTRO_STATUS_SHOWED
    }

    fun View.hide(){
        visibility = View.GONE
    }

    fun View.show(){
        visibility = View.VISIBLE
    }
}

fun alertDialog(
    sActivity: Context?,
    title: String?,
    message: String?,
    yes: String?,
    no: String?,
    cancelable:Boolean,
    dialogInterface: DialogInterface.OnClickListener?
) {
    val builder =
        MaterialAlertDialogBuilder(sActivity!!)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(yes, dialogInterface)
            .setNegativeButton(no, dialogInterface)
    val alertDialog = builder.create()
    alertDialog.show()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}