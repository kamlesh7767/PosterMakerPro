package com.garudpuran.postermakerpro.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.garudpuran.postermakerpro.databinding.FragmentDashboardBinding
import com.garudpuran.postermakerpro.ui.commonui.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.home.HomeTodayOrUpcomingAdapter

class DashboardFragment : Fragment(),HomeTodayOrUpcomingAdapter.HomeTodayOrUpcomingAdapterListener {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val adapter = HomeTodayOrUpcomingAdapter(this)
        adapter.setData(HomeResources.homeCategories())
        binding.rcBusinessPosts.adapter = adapter
        binding.rcFestivalPosts.adapter = adapter
        binding.rcLogosPosts.adapter = adapter
        binding.rcPoliticalPosts.adapter = adapter
        binding.rcTrendingPosts.adapter = adapter
        binding.rcNewsPosts.adapter = adapter
        binding.rcQuotesPosts.adapter = adapter
        binding.rcOtherPosts.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHomeTodayOrUpcomingClicked(item: HomeCategoryModel) {

    }
}