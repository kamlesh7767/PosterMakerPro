package com.garudpuran.postermakerpro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.utils.Utils
import com.bumptech.glide.util.Util
import com.garudpuran.postermakerpro.databinding.FragmentHomeBinding
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.profile.CreatePersonalProfileFragment
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.ViewUtilities
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(),HomeCategoryAdapter.HomeCategoryGridListener,HomeTrendingStoriesAdapter.HomeTrendingStoriesAdapterListener,HomeTodayOrUpcomingAdapter.HomeTodayOrUpcomingAdapterListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val userViewModel : UserViewModel by viewModels ()

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
        observeGetUserProfileData()


        if(userViewModel.onObserveGetUserProfileData().value == null){
            getUserProfileData()
        }


    }



    private fun getUserProfileData() {
val uid = auth.uid
        if(!uid.isNullOrEmpty()){
            observeGetUserProfileData()
            userViewModel.getUserProfile(uid)
        }else{
            ViewUtilities.showToast(requireActivity(),"User does not exist!")
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
                ViewUtilities.showToast(requireActivity(),"Got the data")
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