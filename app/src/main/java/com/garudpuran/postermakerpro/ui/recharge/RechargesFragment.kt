package com.garudpuran.postermakerpro.ui.recharge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.garudpuran.postermakerpro.PaymentActivity
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentProfileBinding
import com.garudpuran.postermakerpro.databinding.FragmentRechargesBinding
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RechargesFragment : Fragment(), RechargesViewPagerAdapter.RechargesViewPagerAdapterListener {
    private var _binding: FragmentRechargesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRechargesBinding.inflate(inflater, container, false)
        observeGetAllPosts()
        getAllPosts()
        return binding.root

    }

    private fun getAllPosts() {
        viewModel.getAllRcg()
    }

    private fun observeGetAllPosts() {
        viewModel.onObserveGetAllRcgResponseData().observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    binding.progress.root.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    binding.progress.root.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    if (it.data!!.isNotEmpty()) {
                        binding.progress.root.visibility = View.GONE
                        initRcView(it.data)
                        Log.d(tag,"Got the Posts.")
                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
    }

    private fun initRcView(data: List<RechargeItem>) {
        val adapter = RechargesViewPagerAdapter(data,getSelectedLanguage(),this)
        binding.rcgViewpager.adapter = adapter

    }

    private fun getSelectedLanguage(): String {
        val authPref = requireActivity().getSharedPreferences(AppPrefConstants.LANGUAGE_PREF, Context.MODE_PRIVATE)
        return authPref.getString("language", "")!!
    }

    override fun rechargesViewPagerAdapterItemClicked(item: RechargeItem) {
       val intent = Intent(requireActivity(),PaymentActivity::class.java)
        intent.putExtra("rechargeId",item.uid)
        startActivity(intent)
    }


}