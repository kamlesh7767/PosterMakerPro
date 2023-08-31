package com.garudpuran.postermakerpro.ui.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.garudpuran.postermakerpro.MainActivity
import com.garudpuran.postermakerpro.databinding.ActivityIntroBinding
import com.garudpuran.postermakerpro.utils.UserReferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {
    private lateinit var binding:ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRcView()
        binding.rcgViewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // This method is called when the ViewPager2 is scrolled.
                // You can use position, positionOffset, and positionOffsetPixels to get scroll information.
            }

            override fun onPageSelected(position: Int) {
                if(position == 4 ){
                    binding.contBtn.visibility = View.VISIBLE
                }
                else{
                    binding.contBtn.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // This method is called when the scroll state changes.
                // The "state" parameter can have three values: ViewPager2.SCROLL_STATE_IDLE (0), ViewPager2.SCROLL_STATE_DRAGGING (1), ViewPager2.SCROLL_STATE_SETTLING (2).
            }
        })


        binding.contBtn.setOnClickListener {
            if(intent.getIntExtra("destination",1) == 1){
                setAsShowed()
                sendToMain()
            }else{
                finish()
            }
        }
    }


    private fun initRcView() {
        val adapter = IntroPagerAdapter()
        binding.rcgViewpager.adapter = adapter

    }

    private fun setAsShowed() {
        val sharedPreference = this.getSharedPreferences(
            UserReferences.USER_INTRO,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString(
            UserReferences.USER_INTRO_STATUS,
            UserReferences.USER_INTRO_STATUS_SHOWED
        )
        editor.apply()
    }

    private fun sendToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }





}