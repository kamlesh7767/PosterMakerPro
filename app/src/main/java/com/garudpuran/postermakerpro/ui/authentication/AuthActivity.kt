package com.garudpuran.postermakerpro.ui.authentication

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.garudpuran.postermakerpro.MainActivity
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityAuthBinding
import com.garudpuran.postermakerpro.databinding.ActivityMainBinding
import com.garudpuran.postermakerpro.ui.commonui.LanguageSelectionBottomSheetFragment
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AuthActivity : AppCompatActivity(),LanguageSelectionBottomSheetFragment.LanguageSelectionListener {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val language = getSelectedLanguage()
        if(language.isEmpty()){
            showCustomDialog()
        }else{
            setAppLocale(this,getSelectedLanguage())
            startActivity(Intent(this , PhoneActivity::class.java))
            finish()
        }

    }

    private fun getSelectedLanguage(): String {
        val authPref = this.getSharedPreferences(AppPrefConstants.LANGUAGE_PREF, Context.MODE_PRIVATE)
        return authPref.getString("language", "")!!
    }

    private fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    private fun showCustomDialog() {
        val customDialog = LanguageSelectionBottomSheetFragment(true, this)
        customDialog.show(supportFragmentManager,"LanguageSelectionBottomSheetFragment")

    }

    override fun onLanguageSelected(language: Int) {
        var lang: String
        if(language == 1){
            setAppLocale(this,"en")
            lang = "en"
        }
        else if(language == 2){
            setAppLocale(this,"hi")
            lang = "hi"
        }

        else{
            setAppLocale(this,"mr")
            lang = "mr"
        }

        val sharedPreference = this.getSharedPreferences(
            AppPrefConstants.LANGUAGE_PREF,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString("language", lang)
        editor.apply()
        finish()
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null){
            setAppLocale(this,getSelectedLanguage())
            startActivity(Intent(this , MainActivity::class.java))
            finish()
        }
    }
}