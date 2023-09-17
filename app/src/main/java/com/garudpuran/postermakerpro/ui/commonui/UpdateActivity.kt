package com.garudpuran.postermakerpro.ui.commonui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.garudpuran.postermakerpro.databinding.ActivityUpdateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.updateAppBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.garudpuran.postermakerpro")
            startActivity(intent)
        }
    }
}