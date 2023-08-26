package com.garudpuran.postermakerpro.ui.editing.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentOptionsAddressBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsFramesBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsLogoBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsMobileNumberBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsProfilePhotoBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsUserNameBinding


class OptionsProfilePhotoFragment : Fragment() {
    private var _binding: FragmentOptionsProfilePhotoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOptionsProfilePhotoBinding.inflate(inflater, container, false)

        return binding.root
    }
}