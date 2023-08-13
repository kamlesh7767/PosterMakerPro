package com.garudpuran.postermakerpro.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.databinding.FragmentHomeBinding
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.editing.EditPostActivity
import com.garudpuran.postermakerpro.ui.editing.EditStoryActivity
import com.garudpuran.postermakerpro.ui.profile.CreatePersonalProfileFragment
import com.garudpuran.postermakerpro.utils.FirebaseStorageConstants
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.UserReferences
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeCategoryAdapter.HomeCategoryGridListener,
    HomeTrendingStoriesAdapter.HomeTrendingStoriesAdapterListener,
    HomeTodayOrUpcomingAdapter.HomeTodayOrUpcomingAdapterListener,
    HomeFeedRcAdapter.HomeFeedClickListener,
    HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener,CreatePersonalProfileFragment.ProfileUpdateListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var userModel = UserPersonalProfileModel()
    private var feedItemList = listOf<FeedItem>()
    private var catSubCatList = listOf<Pair<CategoryItem, List<SubCategoryItem>>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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


    }

    private fun observeData() {
        // Check cache first
        val trendingStoriesCache = homeViewModel.getTrendingStoriesCache()
        val feedItemsCache = homeViewModel.getFeedItemsCacheCache()
        val catSubCatCache = homeViewModel.getCatSubCatCacheCache()
        val userProfilesCache = userViewModel.getUserProfileCache()
        if (trendingStoriesCache.value == null || userProfilesCache.value == null) {
            binding.progress.root.visibility = View.VISIBLE
            fetchData()
        }else{
            initTrendingRcView(trendingStoriesCache.value!!)
            initRcView(feedItemsCache.value!!,catSubCatCache.value!!,userProfilesCache.value!!.likedPosts)
            setUi(userProfilesCache.value!!)
            if(com.garudpuran.postermakerpro.utils.Utils.getProfileBottomPopUpStatus(requireActivity())){
                profileCreate(userProfilesCache.value!!)
            }
        }
    }


    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val trendingStoriesDeferred1 = async { homeViewModel.getAllTrendingStoriesAsync() }
                val trendingStoriesDeferred2 =
                    async { homeViewModel.getAllCategoriesAndSubCategoriesAsync() }
                val trendingStoriesDeferred3 = async { homeViewModel.getAllFeedItemsAsync() }
                val trendingStoriesDeferred4 =
                    async { userViewModel.getUserProfileAsync(auth.uid!!) }

                val results = awaitAll(
                    trendingStoriesDeferred1,
                    trendingStoriesDeferred2,
                    trendingStoriesDeferred3,
                )

                val userDataResults = awaitAll(
                    trendingStoriesDeferred4
                )

                // Check results and proceed
                val allSuccess = results.all { it.status == Status.SUCCESS }
                val allUserDataSuccess = userDataResults.all { it.status == Status.SUCCESS }
                if (allSuccess && allUserDataSuccess) {
                    val dataList = results.flatMap { resource ->
                        resource.data ?: emptyList()
                    }

                    // Now you have the unwrapped data, proceed accordingly
                    val trendingStoriesList = dataList.filterIsInstance<TrendingStoriesItemModel>()
                    feedItemList = dataList.filterIsInstance<FeedItem>()
                    catSubCatList =
                        dataList.filterIsInstance<Pair<CategoryItem, List<SubCategoryItem>>>()
                    if (trendingStoriesList.isNotEmpty()) {
                        binding.progress.root.visibility = View.GONE
                        initTrendingRcView(trendingStoriesList)
                        homeViewModel.setCatSubCatCacheCache(catSubCatList)
                        homeViewModel.setTrendingStoriesCache(trendingStoriesList)
                        homeViewModel.setFeedItemsCacheCache(feedItemList)
                        userViewModel.setUserProfileCache(userDataResults[0].data!!)
                        initRcView(
                            feedItemList,
                            catSubCatList,
                            userDataResults[0].data!!.likedPosts
                        )

                        setUi(userDataResults[0].data!!)
                        if(com.garudpuran.postermakerpro.utils.Utils.getProfileBottomPopUpStatus(requireActivity())){
                            profileCreate(userDataResults[0].data!!)
                        }
                    }
                } else {
                    // Handle errors
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun setUi(data: UserPersonalProfileModel) {
        Glide.with(requireActivity()).load(data.profile_image_url).into(binding.homeUserProfilePic)
        binding.homeUserDespTv.text = data.name

    }

    private fun initTrendingRcView(data: List<TrendingStoriesItemModel>) {
        val adapter = HomeTrendingStoriesAdapter(this)
        adapter.setData(data)
        binding.rcTrending.adapter = adapter
    }

    private fun initRcView(
        data: List<FeedItem>,
        dataSetSecond: List<Pair<CategoryItem, List<SubCategoryItem>>>,
        likedPosts: ArrayList<String>
    ) {
        val adapter = HomeFeedRcAdapter(this, data, dataSetSecond, likedPosts, this)
        binding.feedRcHome.adapter = adapter
    }


    private fun profileCreate(data: UserPersonalProfileModel) {
        if (data.name.isEmpty()) {
            val frag = CreatePersonalProfileFragment(data,this)
            frag.show(childFragmentManager, "CreatePersonalProfileFragment")
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHomeCatClicked(item: HomeCategoryModel) {

    }

    override fun onHomeTodayOrUpcomingClicked(item: HomeCategoryModel) {
    }


    override fun onHomeFeedImageClicked() {
    }

    override fun onHomeFeedCheckOutBtnClicked(item: FeedItem) {
      val intent = Intent(requireActivity(),EditPostActivity::class.java)
        intent.putExtra("imageUrl",item.image_url)
        intent.putExtra("engTitle",item.title_eng)
        intent.putExtra("marTitle",item.title_mar)
        intent.putExtra("hinTitle",item.title_hin)
        startActivity(intent)
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

        val intent = Intent(requireActivity(),EditStoryActivity::class.java)
        intent.putExtra("imageUrl",item.image_url)
        intent.putExtra("engTitle",item.title_eng)
        intent.putExtra("marTitle",item.title_mar)
        intent.putExtra("hinTitle",item.title_hin)
        startActivity(intent)
    }

    override fun onCatSubCatItemAdapterClicked(item: SubCategoryItem) {
        val action = HomeFragmentDirections.actionNavigationHomeToSelectedSubCatItems(
            item.categoryId!!,
            item.Id!!
        )
        findNavController().navigate(action)
    }

    override fun onProfileUpdated() {

    }
}