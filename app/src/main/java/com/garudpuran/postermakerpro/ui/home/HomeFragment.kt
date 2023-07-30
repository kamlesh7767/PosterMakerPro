package com.garudpuran.postermakerpro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.databinding.FragmentHomeBinding
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.profile.CreatePersonalProfileFragment

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

        initTodayOrUpcomingView()
        profileCreate()

        return binding.root
    }

   /* private fun initCategoryView() {
        val adapter = HomeCategoryAdapter(this)
        adapter.setData(HomeResources.homeCategories())
        binding.rcCategories.adapter = adapter
    }*/


    private fun profileCreate(){
        val frag = CreatePersonalProfileFragment()
        frag.show(childFragmentManager,"CreatePersonalProfileFragment")
    }

    private fun initTodayOrUpcomingView() {
        val adapter = HomeFeedRcAdapter()
        binding.feedRcHome.adapter = adapter

        val ad = HomeTrendingStoriesAdapter(this)
        ad.setData(HomeResources.homeCategories())
        binding.rcTrending.adapter = ad
    }

    /*private fun initTrendingView() {
        val adapter = HomeTrendingStoriesAdapter(this)
        adapter.setData(HomeResources.homeCategories())
        binding.rcTrending.adapter = adapter
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHomeCatClicked(item: HomeCategoryModel) {

    }

    override fun onHomeTodayOrUpcomingClicked(item: HomeCategoryModel) {
    }

    override fun onHomeTrendingStoriesClicked(item: HomeCategoryModel) {
        val action = HomeFragmentDirections.actionNavigationHomeToEditPostFragment()
        findNavController().navigate(action)
    }
}