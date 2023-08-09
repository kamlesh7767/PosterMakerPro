package com.garudpuran.postermakerpro.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.databinding.FragmentHomeBinding
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.dashboard.CategoriesFragmentDirections
import com.garudpuran.postermakerpro.ui.profile.CreatePersonalProfileFragment
import com.garudpuran.postermakerpro.utils.FirebaseStorageConstants
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.UserReferences
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(),HomeCategoryAdapter.HomeCategoryGridListener,HomeTrendingStoriesAdapter.HomeTrendingStoriesAdapterListener,HomeTodayOrUpcomingAdapter.HomeTodayOrUpcomingAdapterListener,
    HomeFeedRcAdapter.HomeFeedClickListener,HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val userViewModel : UserViewModel by viewModels ()
    private val homeViewModel : HomeViewModel by viewModels ()
    private  var userModel = UserPersonalProfileModel()
    private  var feedItemList = listOf<FeedItem>()
    private  var catSubCatList = listOf<Pair<CategoryItem,List<SubCategoryItem>>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
      getUserProfileData()
        observeGetAllTrendingStories()
        getAllTrendingStories()
        observeGetCatSubCatPairs()
        getCatSubCatPairs()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeUserProfilePic.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToProfileFragment()
            findNavController().navigate(action)
        }



    }

    private fun getAllTrendingStories() {
        homeViewModel.getAllTrendingStories()
    }

    private fun observeGetAllTrendingStories() {
        homeViewModel.onObserveGetAllTrendingStoriesResponseData().observe(requireActivity()){
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
                        initTrendingRcView(it.data)
                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
    }

    private fun initTrendingRcView(data: List<TrendingStoriesItemModel>) {
        val adapter = HomeTrendingStoriesAdapter(this)
        adapter.setData(data)
        binding.rcTrending.adapter = adapter
    }

    private fun getAllFeed() {
        homeViewModel.getAllFeedItems()
    }

    private fun observeGetAllFeed() {
        homeViewModel.onObserveGetAllFeedItemsResponseData().observe(requireActivity()){
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
                        feedItemList = it.data
                        Log.d("USERDATAFEED",it.data.toString())
                       observeGetCatSubCatPairs()
                        getCatSubCatPairs()

                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
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
                        catSubCatList = it.data
                        Log.d("USERDATACAT",it.data.toString())
                        initRcView(feedItemList,it.data,userModel.likedPosts)

                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
    }

    private fun initRcView(
        data: List<FeedItem>,
        dataSetSecond: List<Pair<CategoryItem, List<SubCategoryItem>>>,
        likedPosts: ArrayList<String>
    ) {
        val adapter = HomeFeedRcAdapter(this,data,dataSetSecond,likedPosts,this)
        binding.feedRcHome.adapter = adapter
    }



    private fun getUserProfileData() {
val uid = auth.uid
        if(!uid.isNullOrEmpty()){
            observeGetUserProfileData()
            userViewModel.getUserProfile(uid)
        }else{
            com.garudpuran.postermakerpro.utils.Utils.showToast(requireActivity(),"User does not exist!")
        }

    }

    private fun observeGetUserProfileData() {
userViewModel.onObserveGetUserProfileData().observe(requireActivity()){
    when (it.status) {
        Status.LOADING -> {
            binding.progress.root.visibility = View.VISIBLE
        }

        Status.ERROR -> {
            binding.progress.root.visibility = View.GONE
        }

        Status.SUCCESS -> {
            if (it.data !=null) {
                binding.progress.root.visibility = View.GONE
                if(com.garudpuran.postermakerpro.utils.Utils.getProfileBottomPopUpStatus(requireActivity())){
                    profileCreate(it.data)
                }
                userModel = it.data
                observeGetAllFeed()
                getAllFeed()
                Log.d("USERDATA",it.data.toString())
            }
        }

        Status.SESSION_EXPIRE -> {

        }
    }
}
    }

    private fun profileCreate(data:UserPersonalProfileModel){
        if(data.name.isEmpty()){
            val frag = CreatePersonalProfileFragment()
            frag.show(childFragmentManager,"CreatePersonalProfileFragment")
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
        val action = HomeFragmentDirections.actionNavigationHomeToEditPostFragment(item.categoryId,item.subCategoryId,item.Id!!,item.image_url)
        findNavController().navigate(action)
    }

    override fun onHomeFeedImageLiked(item: FeedItem) {
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection(FirebaseStorageConstants.MAIN_FEED_NODE).document(item.Id!!).set(item)
            userModel.likedPosts.add(item.image_url)
            db.collection(UserReferences.USER_MAIN_NODE)
                .document(userModel.uid).set(userModel)
        }catch (_:java.lang.Exception){

        }

    }




    override fun onHomeFeedImageUnLiked(item: FeedItem) {

        try {
            val db = FirebaseFirestore.getInstance()
            db.collection(FirebaseStorageConstants.MAIN_FEED_NODE).document(item.Id!!).set(item)
            userModel.likedPosts.remove(item.image_url)
            db.collection(UserReferences.USER_MAIN_NODE)
                .document(userModel.uid).set(userModel)
        }
        catch (_:Exception){

        }

    }

    override fun onHomeTrendingStoriesClicked(item: TrendingStoriesItemModel) {
        val action = HomeFragmentDirections.actionNavigationHomeToEditStoryFragment()
        findNavController().navigate(action)
    }

    override fun onCatSubCatItemAdapterClicked(item: SubCategoryItem) {
        val action = HomeFragmentDirections.actionNavigationHomeToSelectedSubCatItems(item.categoryId!!,item.Id!!)
        findNavController().navigate(action)
    }
}