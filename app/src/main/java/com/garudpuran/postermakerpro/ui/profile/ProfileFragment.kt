package com.garudpuran.postermakerpro.ui.profile

import android.os.Bundle
import android.text.Layout.Directions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileReferEarnBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToReferFragment()
            findNavController().navigate(action)
        }

        binding.profileMyProfileBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToMyProfilesFragment()
            findNavController().navigate(action)
        }

        binding.profileTransactionHistoryBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToTransactionHistoryFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}