package com.garudpuran.postermakerpro.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.databinding.FragmentMyProfilesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfilesFragment : Fragment() {
    private lateinit var _binding: FragmentMyProfilesBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
val adapter = ProfileItemsAdapter()
        binding.myProfileItems.adapter = adapter
    }

}