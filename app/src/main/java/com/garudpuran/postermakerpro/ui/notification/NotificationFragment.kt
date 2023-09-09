package com.garudpuran.postermakerpro.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {
    private lateinit var _binding:FragmentNotificationBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

}