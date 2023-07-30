package com.garudpuran.postermakerpro.ui.transactionhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentTransactionHistoryBinding


class TransactionHistoryFragment : Fragment() {
private lateinit var _binding:FragmentTransactionHistoryBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRCView()
    }

    private fun initRCView() {
        val adapter = TransactionHistoryAdapter()
        binding.transactionHistoryRcView.adapter = adapter
    }
}