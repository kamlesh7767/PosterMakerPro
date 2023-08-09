package com.garudpuran.postermakerpro.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.databinding.FragmentCategoriesBinding
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.home.HomeFeedCatSubCatItemAdapter
import com.garudpuran.postermakerpro.ui.home.HomeTodayOrUpcomingAdapter
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment(),HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener{
    private val homeViewModel : HomeViewModel by viewModels ()
    private var _binding: FragmentCategoriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        observeGetCatSubCatPairs()
        getCatSubCatPairs()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initViews()
    }

    /*private fun initViews() {
        val adapter = HomeTodayOrUpcomingAdapter()
        adapter.setData(HomeResources.homeCategories())
        binding.rcBusinessPosts.adapter = adapter
        binding.rcFestivalPosts.adapter = adapter
        binding.rcLogosPosts.adapter = adapter
        binding.rcPoliticalPosts.adapter = adapter
        binding.rcTrendingPosts.adapter = adapter
        binding.rcNewsPosts.adapter = adapter
        binding.rcQuotesPosts.adapter = adapter
        binding.rcOtherPosts.adapter = adapter
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getCatSubCatPairs() {
        homeViewModel.getAllCategoriesAndSubCategories()
    }

    private fun observeGetCatSubCatPairs() {
        homeViewModel.onObserveGetAllCategoriesAndSubCategoriesResponseData().observe(requireActivity()){
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

                        Log.d("USERDATACAT",it.data.toString())
                        initRcView(it.data)

                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
    }

    private fun initRcView(data: List<Pair<CategoryItem, List<SubCategoryItem>>>) {

        val adapter = CategoriesAdapter(data,this)
        binding.categoriesRc.adapter=adapter

    }

    override fun onCatSubCatItemAdapterClicked(item: SubCategoryItem) {
        val action = CategoriesFragmentDirections.actionNavigationDashboardToSelectedSubCatItems(item.categoryId!!,item.Id!!)
        findNavController().navigate(action)
    }
}