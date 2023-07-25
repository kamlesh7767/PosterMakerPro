package com.garudpuran.postermakerpro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.garudpuran.postermakerpro.databinding.FragmentHomeBinding
import com.garudpuran.postermakerpro.ui.commonui.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.commonui.HomeResources

class HomeFragment : Fragment(),HomeCategoryAdapter.HomeCategoryGridListener,HomeTrendingStoriesAdapter.HomeTrendingStoriesAdapterListener,HomeTodayOrUpcomingAdapter.HomeTodayOrUpcomingAdapterListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initTrendingView()
        initTodayOrUpcomingView()
        initCategoryView()

        return binding.root
    }

    private fun initCategoryView() {
        val adapter = HomeCategoryAdapter(this)
        adapter.setData(HomeResources.homeCategories())
        binding.rcCategories.adapter = adapter
    }

    private fun initTodayOrUpcomingView() {
        val adapter = HomeTodayOrUpcomingAdapter(this)
        adapter.setData(HomeResources.homeCategories())
        binding.rcTodayOrUpcoming.adapter = adapter
    }

    private fun initTrendingView() {
        val adapter = HomeTrendingStoriesAdapter(this)
        adapter.setData(HomeResources.homeCategories())
        binding.rcTrending.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHomeCatClicked(item: HomeCategoryModel) {
        TODO("Not yet implemented")
    }

    override fun onHomeTodayOrUpcomingClicked(item: HomeCategoryModel) {
        TODO("Not yet implemented")
    }

    override fun onHomeTrendingStoriesClicked(item: HomeCategoryModel) {
        TODO("Not yet implemented")
    }
}