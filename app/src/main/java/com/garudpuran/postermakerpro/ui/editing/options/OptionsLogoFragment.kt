package com.garudpuran.postermakerpro.ui.editing.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentOptionsAddressBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsFramesBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsLogoBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsMobileNumberBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsUserNameBinding


class OptionsLogoFragment : Fragment() {
    private var _binding: FragmentOptionsLogoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOptionsLogoBinding.inflate(inflater, container, false)
        binding.editFragOptionsIconHideShowImageBtn.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {

            } else {
            }

        }

        binding.editFragOptionsIconResetImageBtn.setOnClickListener {

        }


        binding.editIconImageSizeSlider.addOnChangeListener { slider, value, fromUser ->

        }

        // options icon
        binding.editFragOptionsIconChangeImageBtn.setOnClickListener {

        }

        return binding.root
    }
}