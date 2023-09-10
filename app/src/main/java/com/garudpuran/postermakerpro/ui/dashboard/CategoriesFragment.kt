package com.garudpuran.postermakerpro.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentCategoriesBinding
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.ErrorDialogFrag
import com.garudpuran.postermakerpro.ui.editing.EditStoryActivity
import com.garudpuran.postermakerpro.ui.home.HomeFeedCatSubCatItemAdapter
import com.garudpuran.postermakerpro.ui.home.HomeTrendingStoriesAdapter
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.ui.intro.IntroActivity
import com.garudpuran.postermakerpro.ui.profile.CreatePersonalProfileFragment
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment : Fragment(), HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener,
    CreatePersonalProfileFragment.ProfileUpdateListener,
    HomeTrendingStoriesAdapter.HomeTrendingStoriesAdapterListener,
    ErrorDialogFrag.ErrorDialogListener {
    private val homeViewModel : HomeViewModel by viewModels ()
    private val userViewModel: UserViewModel by viewModels()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private  val args: CategoriesFragmentArgs by navArgs()
    private var doubleBackToExitPressedOnce = false
    private  var searchedCatSubCatPair = mutableListOf<Pair<CategoryItem, MutableList<SubCategoryItem>>>()
    private  var catSubCatPairList = listOf< Pair<CategoryItem, MutableList<SubCategoryItem>>>()

    private fun showAd() {
        val adRequest = AdRequest.Builder().build()
        binding.profileBannerAdV.loadAd(adRequest)
        val adRequest2 = AdRequest.Builder().build()
        binding.catBannerAdV.loadAd(adRequest2)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish()
                    return
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(
                    requireActivity(),
                    "Please click BACK again to exit",
                    Toast.LENGTH_SHORT
                ).show()

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    doubleBackToExitPressedOnce = false
                }, 2000)
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

        binding.homeUserProfilePic.setOnClickListener {
            val action = CategoriesFragmentDirections.actionNavigationCategoriesToProfileFragment()
            findNavController().navigate(action)
        }

        binding.infoBtn.setOnClickListener {
            sendToIntro()
        }

    }

    private fun sendToIntro() {
        val intent = Intent(requireActivity(), IntroActivity::class.java)
        intent.putExtra("destination",2)
        startActivity(intent)
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

                val catSubCatDeferred = async { homeViewModel.getAllCategoriesAndSubCategoriesAsync() }
                val trendingStoriesDeferred = async { homeViewModel.getAllTrendingStoriesAsync() }
                val userProfileDeferred = async { userViewModel.getUserProfileAsync(auth.uid!!) }

                val results = awaitAll(
                    catSubCatDeferred
                )

                val userResults = awaitAll(userProfileDeferred)
                val trendingResults = awaitAll(trendingStoriesDeferred)

                // Check results and proceed
                val allSuccess = results.all { it.status == Status.SUCCESS }
                val userDataSuccess = userResults.all { it.status == Status.SUCCESS }
                val trendingDataSuccess = trendingResults[0].status == Status.SUCCESS

                // Set userData if userDataSuccess
                if (userDataSuccess) {
                   val  userData = userResults[0].data as UserPersonalProfileModel
                    userViewModel.setUserProfileCache(userData)
                    setUi(userData)

                    if (Utils.getProfileBottomPopUpStatus(requireActivity())) {
                        profileCreate(userData)
                    }
                }
                else{

                    if(auth.currentUser ==null){
                        setErrorDialog()
                    }

                    binding.homeWelComeTv.visibility = View.GONE
                    binding.homeUserNameTv.visibility = View.GONE
                    binding.homeUserProfilePic.visibility = View.GONE

                }

                if (trendingDataSuccess) {
                    val trendingStoriesData =
                        trendingResults[0].data
                    val visibleTrendingStoriesList = trendingStoriesData!!.filter { it.visibility!! }
                    if (visibleTrendingStoriesList.isNotEmpty()) {
                        initTrendingRcView(visibleTrendingStoriesList)
                        homeViewModel.setTrendingStoriesCache(visibleTrendingStoriesList)
                    } else {
                        // no trending stories
                        binding.rcTrending.visibility = View.GONE
                        binding.textView6.visibility = View.GONE
                    }
                }

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
        val trendingStoriesCache = homeViewModel.getTrendingStoriesCache()
        val userProfilesCache = userViewModel.getUserProfileCache()
        if (userProfilesCache.value == null ||trendingStoriesCache.value == null || catSubCatCache.value == null ) {
            binding.progress.root.visibility = View.VISIBLE
            fetchData()
        }else{
            initRcView(catSubCatCache.value!!)
            initTrendingRcView(trendingStoriesCache.value!!)
            setUi(userProfilesCache.value!!)
            if (Utils.getProfileBottomPopUpStatus(
                    requireActivity()
                )
            ) {
                profileCreate(userProfilesCache.value!!)
            }
        }
    }

    private fun setUi(data: UserPersonalProfileModel) {
        if(data.name.isEmpty()){

        }else{
            Glide.with(requireActivity()).load(data.profile_image_url).into(binding.homeUserProfilePic)
            binding.homeUserNameTv.text = data.name
            binding.homeWelComeTv.text = getString(R.string.welcome)
        }
        showAd()

    }

    private fun initTrendingRcView(data: List<TrendingStoriesItemModel>) {
        val adapter = HomeTrendingStoriesAdapter(this, getSelectedLanguage())
        adapter.setData(data)
        binding.rcTrending.adapter = adapter
    }

    private fun profileCreate(data: UserPersonalProfileModel) {
        if (data.name.isEmpty()) {
            val frag = CreatePersonalProfileFragment(data, this)
            frag.show(childFragmentManager, "CreatePersonalProfileFragment")
        }

    }

    private fun initRcView(data: List<Pair<CategoryItem, List<SubCategoryItem>>>) {
        val adapter = CategoriesAdapter(data,this,getSelectedLanguage())
        binding.categoriesRc.adapter=adapter
        binding.categoriesRc.visibility = View.VISIBLE
        showAd()
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

    override fun onProfileUpdated() {

    }

    override fun onHomeTrendingStoriesClicked(item: TrendingStoriesItemModel) {
        val intent = Intent(requireActivity(), EditStoryActivity::class.java)
        intent.putExtra("trending_story_id", item.Id)
        startActivity(intent)
    }
}