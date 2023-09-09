package com.garudpuran.postermakerpro.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentSearchResultsBinding
import com.garudpuran.postermakerpro.databinding.FragmentSelectedSubCatItemsBinding

class SearchResultsFragment : Fragment() {
    private lateinit var _binding: FragmentSearchResultsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)

        return binding.root
    }

}