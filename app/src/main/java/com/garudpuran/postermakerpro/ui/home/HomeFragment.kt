package com.garudpuran.postermakerpro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.databinding.FragmentHomeBinding
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.profile.CreatePersonalProfileFragment
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(),HomeCategoryAdapter.HomeCategoryGridListener,HomeTrendingStoriesAdapter.HomeTrendingStoriesAdapterListener,HomeTodayOrUpcomingAdapter.HomeTodayOrUpcomingAdapterListener,HomeFeedRcAdapter.HomeFeedClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val userViewModel : UserViewModel by viewModels ()
    private val homeViewModel : HomeViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initTodayOrUpcomingView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGetAllFeed()
        getAllFeed()
if(com.garudpuran.postermakerpro.utils.Utils.getProfileBottomPopUpStatus(requireActivity())){
    if(userViewModel.onObserveGetUserProfileData().value == null){
        getUserProfileData()
    }

}


        binding.homeUserProfilePic.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToProfileFragment()
            findNavController().navigate(action)
        }



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
                        initRcView(it.data)

                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
    }

    private fun initRcView(data: List<FeedItem>) {
        val adapter = HomeFeedRcAdapter(this)
        adapter.setData(data)
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
               profileCreate(it.data)
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

    private fun initTodayOrUpcomingView() {


        val ad = HomeTrendingStoriesAdapter(this)
        ad.setData(HomeResources.homeCategories())
        binding.rcTrending.adapter = ad
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHomeCatClicked(item: HomeCategoryModel) {

    }

    override fun onHomeTodayOrUpcomingClicked(item: HomeCategoryModel) {
    }

    override fun onHomeTrendingStoriesClicked(item: HomeCategoryModel) {
        val action = HomeFragmentDirections.actionNavigationHomeToEditStoryFragment()
        findNavController().navigate(action)
    }

    override fun onHomeFeedImageClicked() {
    }

    override fun onHomeFeedImageLiked(item: FeedItem) {
        homeViewModel.likeFeedItem(item)
    }


    override fun onHomeFeedImageUnLiked() {

    }
}