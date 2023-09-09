package com.garudpuran.postermakerpro

import android.app.Application
import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val authPref = this.getSharedPreferences(AppPrefConstants.LANGUAGE_PREF, Context.MODE_PRIVATE)
        val language =  authPref.getString("language", "")!!
        if(language.isNotEmpty()){
            val locale = Locale(language)
            Locale.setDefault(locale)
        }

    }

}