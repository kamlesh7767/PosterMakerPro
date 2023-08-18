package com.garudpuran.postermakerpro

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.garudpuran.postermakerpro.databinding.ActivityMainBinding
import com.garudpuran.postermakerpro.ui.commonui.LanguageSelectionBottomSheetFragment
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setItemSelected(R.id.navigation_home)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item) {
                R.id.navigation_home -> {
                    navController.clearBackStack(navController.graph.startDestinationId)
                    navController.navigate(R.id.navigation_home)

                }

                R.id.navigation_categories -> {
                    navController.popBackStack(navController.graph.startDestinationId, true)
                    navController.navigate(R.id.navigation_categories)

                }

                R.id.profile_fragment -> {
                    navController.popBackStack(navController.graph.startDestinationId, true)
                    navController.navigate(R.id.profile_fragment)

                }

                R.id.navigation_customFragment -> {
                    navController.popBackStack(navController.graph.startDestinationId, true)
                    navController.navigate(R.id.navigation_customFragment)

                }
            }


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

    override fun onStart() {
        super.onStart()
        setAppLocale(this,getSelectedLanguage())
    }

}