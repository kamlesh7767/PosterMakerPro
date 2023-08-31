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


class OptionsLogoFragment(private val mListener: OptionsLogoListener) : Fragment() {
    private var _binding: FragmentOptionsLogoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsLogoBinding.inflate(inflater, container, false)
        binding.editFragOptionsIconHideShowImageBtn.setOnCheckedChangeListener { p0, p1 ->
            mListener.logoHideShowBtnClicked(p1)

        }

        binding.editFragOptionsIconResetImageBtn.setOnClickListener {
            mListener.logoResetClicked()
        }

        binding.editFragOptionsLogoBgRmvBtn.setOnClickListener {
            mListener.logoPicBgRemove()
        }


        binding.editIconImageSizeSlider.addOnChangeListener { slider, value, fromUser ->
            mListener.logoSizeSliderClicked(value)
        }

        // options icon
        binding.editFragOptionsIconChangeImageBtn.setOnClickListener {
            mListener.logoChangeClicked()
        }

        return binding.root
    }

    interface OptionsLogoListener {
        fun logoHideShowBtnClicked(p1: Boolean)
        fun logoSizeSliderClicked(value: Float)

        fun logoChangeClicked()

        fun logoResetClicked()

        fun logoPicBgRemove()
    }
}