package com.garudpuran.postermakerpro.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentHomeBinding
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.SearchModel
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.ErrorDialogFrag
import com.garudpuran.postermakerpro.ui.editing.EditStoryActivity
import com.garudpuran.postermakerpro.ui.profile.CreatePersonalProfileFragment
import com.garudpuran.postermakerpro.ui.profile.SelectProfessionalProfileBottomSheetFrag
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.FirebaseStorageConstants
import com.garudpuran.postermakerpro.utils.SearchItemType
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.UserReferences
import com.garudpuran.postermakerpro.utils.Utils.getProfileBottomPopUpStatus
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(),
    HomeTrendingStoriesAdapter.HomeTrendingStoriesAdapterListener,
    HomeFeedRcAdapter.HomeFeedClickListener,
    CreatePersonalProfileFragment.ProfileUpdateListener,
    SearchItemsRCAdapter.SearchItemClickListener , ErrorDialogFrag.ErrorDialogListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var userModel = UserPersonalProfileModel()
    private var searchItemList = listOf<SearchModel>()
    private var doubleBackToExitPressedOnce = false
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

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

        binding.homeUserProfilePic.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToProfileFragment()
            findNavController().navigate(action)
        }

        binding.homeNotificationIcon.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNotificationFragment()
            findNavController().navigate(action)
        }

        binding.homeSearchEt.addTextChangedListener {
            val s = it?.trim()
            if (s!!.isNotEmpty()) {
                initSearch(s)
            } else {
                binding.homeSearchRc.visibility = View.GONE
            }
        }

    }

    private fun initSearch(s: CharSequence) {
        val foundItemsList = mutableListOf<SearchModel>()
        searchItemList.forEach { it ->
            if (it.title_eng.lowercase()
                    .startsWith(s.toString().lowercase()) || it.title_hin.lowercase()
                    .startsWith(s.toString().lowercase()) || it.title_mar.lowercase()
                    .startsWith(s.toString().lowercase())
                && it.visibility!!
            ) {
                foundItemsList.add(it)
            }
        }
        if (foundItemsList.isNotEmpty()) {
            initSearchRcView(foundItemsList)
        } else {
            binding.homeSearchRc.visibility = View.GONE
            foundItemsList.clear()
        }


    }

    private fun initSearchRcView(foundItemsList: MutableList<SearchModel>) {
        val adapter = SearchItemsRCAdapter(this, getSelectedLanguage())
        adapter.setItems(foundItemsList)
        binding.homeSearchRc.adapter = adapter
        binding.homeSearchRc.visibility = View.VISIBLE
    }

    private fun observeData() {
        // Check cache first
        val trendingStoriesCache = homeViewModel.getTrendingStoriesCache()
        val feedItemsCache = homeViewModel.getFeedItemsCacheCache()
        val searchItems = homeViewModel.getAllSearchItemsCache()
        val userProfilesCache = userViewModel.getUserProfileCache()
        if (trendingStoriesCache.value == null || userProfilesCache.value == null || searchItems.value == null) {
            binding.progress.root.visibility = View.VISIBLE
            fetchData()
        } else {
            initTrendingRcView(trendingStoriesCache.value!!)
            initRcView(feedItemsCache.value!!, userProfilesCache.value!!.likedPosts)
            setUi(userProfilesCache.value!!)
            searchItemList = searchItems.value!!
            if (getProfileBottomPopUpStatus(
                    requireActivity()
                )
            ) {
                profileCreate(userProfilesCache.value!!)
            }
        }
    }


    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            var userData: UserPersonalProfileModel? = null
            try {
                val trendingStoriesDeferred = async { homeViewModel.getAllTrendingStoriesAsync() }
                val searchItemsDeferred = async { homeViewModel.getAllSearchItems() }
                val feedItemsDeferred = async { homeViewModel.getAllFeedItemsAsync() }
                val userProfileDeferred = async { userViewModel.getUserProfileAsync(auth.uid!!) }

                val results = awaitAll(
                    searchItemsDeferred,
                    feedItemsDeferred
                )

                val userResults = awaitAll(userProfileDeferred)
                val trendingResults = awaitAll(trendingStoriesDeferred)

                // Check results and proceed
                val allSuccess = results.all { it.status == Status.SUCCESS }
                val userDataSuccess = userResults.all { it.status == Status.SUCCESS }
                val trendingDataSuccess = trendingResults[0].status == Status.SUCCESS

                // Set userData if userDataSuccess
                if (userDataSuccess) {
                    userData = userResults[0].data as UserPersonalProfileModel
                    userViewModel.setUserProfileCache(userData)
                    setUi(userData)

                    if (getProfileBottomPopUpStatus(requireActivity())) {
                        profileCreate(userData)
                    }
                }else{
                    if(auth.currentUser ==null){
                        setErrorDialog()
                    }

                    binding.homeWelComeTv.visibility = View.GONE
                    binding.homeUserNameTv.visibility = View.GONE
                    binding.homeUserProfilePic.visibility = View.GONE
                    if (searchItemList.isEmpty()) {
                        // Handle search items
                        binding.homeTopCard.visibility = View.GONE
                    }
                }

                if (allSuccess) {
                    binding.progress.root.visibility = View.GONE
                    val dataList = results.flatMap { resource -> resource.data ?: emptyList() }
                    val feedItemList = dataList.filterIsInstance<FeedItem>()
                    searchItemList = dataList.filterIsInstance<SearchModel>()
                    homeViewModel.setAllSearchItemsCache(searchItemList)
                    val visibleFeedList = feedItemList.filter { it.visibility!! }

                    if (visibleFeedList.isNotEmpty()) {
                        homeViewModel.setFeedItemsCacheCache(visibleFeedList)
                        if (userData != null) {
                            initRcView(
                                visibleFeedList,
                                userData.likedPosts
                            )
                        }else{
                            initRcView(
                                visibleFeedList,
                                null
                            )
                        }
                    } else {
                        // no feed
                    }
                } else {
                    setErrorDialog()
                }
                if (searchItemList.isEmpty()) {
                    // Handle search items
                    binding.homeSearchEt.visibility = View.GONE
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
                }else{
                    setErrorDialog()
                }
            } catch (e: Exception) {
                // Handle exceptions
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

    private fun setUi(data: UserPersonalProfileModel) {
        if(data.name.isEmpty()){

        }else{
            Glide.with(requireActivity()).load(data.profile_image_url).into(binding.homeUserProfilePic)
            binding.homeUserNameTv.text = data.name
            binding.homeWelComeTv.text = getString(R.string.welcome)
        }


    }

    private fun initTrendingRcView(data: List<TrendingStoriesItemModel>) {
        val adapter = HomeTrendingStoriesAdapter(this, getSelectedLanguage())
        adapter.setData(data)
        binding.rcTrending.adapter = adapter
    }

    private fun initRcView(
        data: List<FeedItem>,
        likedPosts: ArrayList<String>?
    ) {
        val adapter = HomeFeedRcAdapter(this, data, likedPosts!!, getSelectedLanguage())
        binding.feedRcHome.adapter = adapter
    }

    private fun getSelectedLanguage(): String {
        val authPref = requireActivity().getSharedPreferences(
            AppPrefConstants.LANGUAGE_PREF,
            Context.MODE_PRIVATE
        )
        return authPref.getString("language", "")!!
    }


    private fun profileCreate(data: UserPersonalProfileModel) {
        if (data.name.isEmpty()) {
            val frag = CreatePersonalProfileFragment(data, this)
            frag.show(childFragmentManager, "CreatePersonalProfileFragment")
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onHomeFeedImageClicked() {
    }

    override fun onHomeFeedCheckOutBtnClicked(item: FeedItem) {

        if (item.createdByAdmin){
            val frag = SelectProfessionalProfileBottomSheetFrag(
                item.image_url,
                item.title_eng,
                item.title_mar,
                item.title_hin,
                item.categoryId,
                item.subCategoryId,
                item.postId
            )
            frag.show(childFragmentManager, "SelectProfessionalProfileBottomSheetFrag")
        }else{
            val frag = SelectProfessionalProfileBottomSheetFrag(
                item.original_image_url,
                item.title_eng,
                item.title_mar,
                item.title_hin,
                item.categoryId,
                item.subCategoryId,
                item.postId
            )
            frag.show(childFragmentManager, "SelectProfessionalProfileBottomSheetFrag")
        }



    }

    override fun onHomeFeedImageLiked(item: FeedItem) {
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection(FirebaseStorageConstants.MAIN_FEED_NODE).document(item.Id!!).set(item)
            userModel.likedPosts.add(item.image_url)
            db.collection(UserReferences.USER_MAIN_NODE)
                .document(userModel.uid).set(userModel)
        } catch (_: java.lang.Exception) {

        }

    }


    override fun onHomeFeedImageUnLiked(item: FeedItem) {

        try {
            val db = FirebaseFirestore.getInstance()
            db.collection(FirebaseStorageConstants.MAIN_FEED_NODE).document(item.Id!!).set(item)
            userModel.likedPosts.remove(item.image_url)
            db.collection(UserReferences.USER_MAIN_NODE)
                .document(userModel.uid).set(userModel)
        } catch (_: Exception) {

        }

    }

    override fun onHomeTrendingStoriesClicked(item: TrendingStoriesItemModel) {

        val intent = Intent(requireActivity(), EditStoryActivity::class.java)
        intent.putExtra("trending_story_id", item.Id)
        startActivity(intent)
    }

    override fun onProfileUpdated() {

    }

    override fun onSearchItemClicked(item: SearchModel) {
        when (item.type) {
            SearchItemType.POST_TYPE -> {
                val action = HomeFragmentDirections.actionNavigationHomeToSelectedSubCatItems(
                    item.categoryId!!,
                    item.subCategoryId
                )
                findNavController().navigate(action)

            }

            SearchItemType.CAT_TYPE -> {
                val action =
                    HomeFragmentDirections.actionNavigationHomeToNavigationCategories(item.Id!!)
                findNavController().navigate(action)
            }

            SearchItemType.SUB_CAT_TYPE -> {
                val action = HomeFragmentDirections.actionNavigationHomeToSelectedSubCatItems(
                    item.categoryId!!,
                    item.Id!!
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDialogDismissed() {
        startActivity(requireActivity().intent)
    }
}