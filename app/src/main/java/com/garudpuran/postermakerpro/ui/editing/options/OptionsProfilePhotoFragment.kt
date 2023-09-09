package com.garudpuran.postermakerpro.ui.editing.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.databinding.FragmentOptionsProfilePhotoBinding


class OptionsProfilePhotoFragment(private val mListener:OptionsProfilePicListener) : Fragment() {
    private var _binding: FragmentOptionsProfilePhotoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsProfilePhotoBinding.inflate(inflater, container, false)
       binding.editFragOptionsProfileHideShowImageBtn.setOnCheckedChangeListener { p0, p1 ->
           mListener.profilePicHideShowBtnClicked(p1)

        }

        binding.editFragOptionsProfileResetImageBtn.setOnClickListener {
mListener.profilePicResetClicked()
        }


        binding.editProfileImageSizeSlider.addOnChangeListener { slider, value, fromUser ->
mListener.profilePicSizeSliderClicked(value)
        }


        binding.editFragOptionsProfileChangeImageBtn.setOnClickListener {
mListener.profilePicChangeClicked()
        }
        binding.editFragOptionsProfileBgRmvBtn.setOnClickListener {
            mListener.profilePicBgRemove()
        }


        return binding.root
    }

    interface OptionsProfilePicListener{
        fun profilePicHideShowBtnClicked(p1: Boolean)
        fun profilePicSizeSliderClicked(value: Float)

        fun profilePicChangeClicked()

        fun profilePicResetClicked()
        fun profilePicBgRemove()
    }


}