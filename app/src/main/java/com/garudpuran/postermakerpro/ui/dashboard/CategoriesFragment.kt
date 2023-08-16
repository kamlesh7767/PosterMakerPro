package com.garudpuran.postermakerpro.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.databinding.FragmentCategoriesBinding
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.ui.home.HomeFeedCatSubCatItemAdapter
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment : Fragment(),HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener{
    private val homeViewModel : HomeViewModel by viewModels ()
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(requireActivity().intent)
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        observeData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val trendingStoriesDeferred2 =
                    async { homeViewModel.getAllCategoriesAndSubCategoriesAsync() }

                val results = awaitAll(
                    trendingStoriesDeferred2,
                )

                // Check results and proceed
                val allSuccess = results.all { it.status == Status.SUCCESS }

                if (allSuccess ) {
                    val dataList = results.flatMap { resource ->
                        resource.data ?: emptyList()
                    }


                    if (dataList.isNotEmpty()) {
                        binding.progress.root.visibility = View.GONE
                        homeViewModel.setCatSubCatCacheCache(dataList)
                        initRcView(dataList)
                    }
                } else {
                    // Handle errors
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun observeData() {
        // Check cache first
        val catSubCatCache = homeViewModel.getCatSubCatCacheCache()
        if (catSubCatCache.value == null ) {
            binding.progress.root.visibility = View.VISIBLE
            fetchData()
        }else{
            initRcView(catSubCatCache.value!!)
        }
    }

    private fun initRcView(data: List<Pair<CategoryItem, List<SubCategoryItem>>>) {
        val adapter = CategoriesAdapter(data,this,getSelectedLanguage())
        binding.categoriesRc.adapter=adapter
    }

    private fun getSelectedLanguage(): String {
        val authPref = requireActivity().getSharedPreferences(AppPrefConstants.LANGUAGE_PREF, Context.MODE_PRIVATE)
        return authPref.getString("language", "")!!
    }


    override fun onCatSubCatItemAdapterClicked(item: SubCategoryItem) {
        val action = CategoriesFragmentDirections.actionNavigationDashboardToSelectedSubCatItems(item.categoryId!!,item.Id!!)
        findNavController().navigate(action)
    }
}