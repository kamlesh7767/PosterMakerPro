package com.garudpuran.postermakerpro.ui.dashboard

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.garudpuran.postermakerpro.databinding.FragmentCategoriesBinding
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.SearchModel
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.ui.commonui.ErrorDialogFrag
import com.garudpuran.postermakerpro.ui.home.HomeFeedCatSubCatItemAdapter
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.ui.subcat.SelectedSubCatItemsFragmentArgs
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment : Fragment(),HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener,
    ErrorDialogFrag.ErrorDialogListener {
    private val homeViewModel : HomeViewModel by viewModels ()
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private  val args: CategoriesFragmentArgs by navArgs()
    private  var searchedCatSubCatPair = mutableListOf<Pair<CategoryItem, MutableList<SubCategoryItem>>>()
    private  var catSubCatPairList = listOf< Pair<CategoryItem, MutableList<SubCategoryItem>>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(requireActivity().intent)
                requireActivity().finish()
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
binding.catFragSearchEt.addTextChangedListener {
    val s = it?.trim()
    if (s!!.isNotEmpty()) {
        initSearch(s)
    } else {
       initRcView(catSubCatPairList)
    }
}
    }

    private fun initSearch(s: CharSequence) {
        val lowerCaseSearch = s.toString().lowercase()
        val matchingPairs = catSubCatPairList.filter { pair ->
            val categoryMatches = pair.first.title_eng.lowercase().startsWith(lowerCaseSearch) ||
                    pair.first.title_hin.lowercase().startsWith(lowerCaseSearch) ||
                    pair.first.title_mar.lowercase().startsWith(lowerCaseSearch)

            val subCategoryMatches = pair.second.any { sub ->
                sub.title_eng.lowercase().startsWith(lowerCaseSearch) ||
                        sub.title_hin.lowercase().startsWith(lowerCaseSearch) ||
                        sub.title_mar.lowercase().startsWith(lowerCaseSearch)
            }

            categoryMatches || subCategoryMatches
        }

        if (matchingPairs.isNotEmpty()) {
            initRcView(matchingPairs)
        } else {
            binding.categoriesRc.visibility = View.GONE
        }
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
                         catSubCatPairList = dataList
                            .filter { it.first.visibility == true }
                            .map { (category, subCategoryList) ->
                                val visibleSubCategoryList = subCategoryList.filter { it.visibility == true }
                                category to visibleSubCategoryList.toMutableList()
                            }
                        binding.progress.root.visibility = View.GONE
                        homeViewModel.setCatSubCatCacheCache(catSubCatPairList)

                        if(args.catId.isNotEmpty()){
                           catSubCatPairList.forEach {
                               if(it.first.Id == args.catId){
                                  searchedCatSubCatPair.add(it)
                               }
                           }
                            initRcView(searchedCatSubCatPair)

                        }else{
                            initRcView(catSubCatPairList)
                        }
                    }
                } else {
                    setErrorDialog()
                }
            } catch (e: Exception) {
                Log.d("CATSUB",e.toString())
                setErrorDialog()
            }
        }
    }

    private fun setErrorDialog() {
        binding.progress.root.visibility = View.GONE
        val errorD = ErrorDialogFrag(this)
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.add(errorD, "ErrorDialogFrag")
        fragmentTransaction.commitAllowingStateLoss()
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
        binding.categoriesRc.visibility = View.VISIBLE
    }

    private fun getSelectedLanguage(): String {
        val authPref = requireActivity().getSharedPreferences(AppPrefConstants.LANGUAGE_PREF, Context.MODE_PRIVATE)
        return authPref.getString("language", "")!!
    }


    override fun onCatSubCatItemAdapterClicked(item: SubCategoryItem) {
        val action = CategoriesFragmentDirections.actionNavigationDashboardToSelectedSubCatItems(item.categoryId!!,item.Id!!)
        findNavController().navigate(action)
    }

    override fun onDialogDismissed() {
        startActivity(requireActivity().intent)
    }
}