package com.garudpuran.postermakerpro

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.garudpuran.postermakerpro.databinding.ActivityMainBinding
import com.garudpuran.postermakerpro.models.AdminModel
import com.garudpuran.postermakerpro.ui.commonui.UpdateActivity
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var interstitialAd: InterstitialAd? = null
    private val handler = Handler()
    private val adDisplayInterval = 3 * 60 * 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
      startAdDisplayLoop()
        checkVersionUpdate()
       // window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

    }



    private fun checkVersionUpdate() {
       val db= FirebaseFirestore.getInstance().collection("ADMIN").document("ADMINKEYS")
        db.get().addOnSuccessListener {
            if(it.exists()){
                val data = it.toObject(AdminModel::class.java)
                Log.d("VERSION_CODE","VersionCode::${data!!.versionCode}")
                if(data!!.versionCode>BuildConfig.VERSION_CODE){
                    startActivity(Intent(this,UpdateActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-4135756483743089/1376225141",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    // Start displaying the ad periodically
                    interstitialAd!!.show(this@MainActivity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle ad load failure
                    interstitialAd = null
                }
            }
        )
    }

    private val adDisplayRunnable = object : Runnable {
        override fun run() {
        loadInterstitialAd()
            // Schedule the next ad display
            handler.postDelayed(this, adDisplayInterval.toLong())
        }
    }

    private fun startAdDisplayLoop() {
        val initialDelay = 30*1000L
        handler.postDelayed(adDisplayRunnable, initialDelay)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the ad display callbacks to prevent memory leaks
        handler.removeCallbacks(adDisplayRunnable)
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

    override fun onStart() {
        super.onStart()
        setAppLocale(this,getSelectedLanguage())
    }

}