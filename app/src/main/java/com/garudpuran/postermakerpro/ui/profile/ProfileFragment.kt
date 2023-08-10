package com.garudpuran.postermakerpro.ui.profile

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
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentProfileBinding
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.authentication.PhoneActivity
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(),CreatePersonalProfileFragment.ProfileUpdateListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var userData = UserPersonalProfileModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
observeUserData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editPersonalProfileBtn.setOnClickListener {
            if(userData.uid.isEmpty()){
                observeUserData()
            }else{
                profileCreate(userData)
            }

        }

        binding.profileReferEarnBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToReferFragment()
            findNavController().navigate(action)
        }

        binding.profileMyProfileBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToMyProfilesFragment()
            findNavController().navigate(action)
        }

        binding.profileTransactionHistoryBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToTransactionHistoryFragment()
            findNavController().navigate(action)
        }

        binding.profileSignOutBtn.setOnClickListener {
            val builder =
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Are you sure?")
                    .setMessage("You will be signed out!")
                    .setCancelable(true)
                    .setPositiveButton(
                        "yes"
                    ) { _, _ ->
                        auth.signOut()
                        val intent = Intent(requireActivity(),PhoneActivity().javaClass)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .setNegativeButton("no", null)
            val alertDialog = builder.create()
            alertDialog.show()

        }
    }

    private fun profileCreate(data: UserPersonalProfileModel) {
        val frag = CreatePersonalProfileFragment(data,this)
       if(childFragmentManager.fragments.contains(frag)){
           childFragmentManager.popBackStack()
       }
            frag.show(childFragmentManager, "CreatePersonalProfileFragment")

    }

    private fun observeUserData() {
        val userProfilesCache = userViewModel.getUserProfileCache()
        if (userProfilesCache.value == null) {
            fetchData()
        }else{
            setUi(userProfilesCache.value!!)
        }
    }


    private fun setUi(data: UserPersonalProfileModel){
        val userImgrl = data.profile_image_url
        val userMobile = data.mobile_number
        val userName = data.name
        if(userImgrl.isNotEmpty()){
            Glide.with(requireActivity()).load(userImgrl).placeholder(R.drawable.naruto).into(binding.profileFragUserPicCiv)
        }

        if(userMobile.isNotEmpty()){
            binding.profileUserMobileTv.text = userMobile
        }else{
            binding.profileUserMobileTv.visibility = View.INVISIBLE
        }

        if(userName.isNotEmpty()){
            binding.profileFragUserNameTv.text = userName
        }else{
            binding.profileFragUserNameTv.visibility = View.INVISIBLE
        }
    }


    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val trendingStoriesDeferred4 =
                    async { userViewModel.getUserProfileAsync(auth.uid!!) }

                val userDataResults = awaitAll(
                    trendingStoriesDeferred4
                )

                // Check results and proceed
                val allUserDataSuccess = userDataResults.all { it.status == Status.SUCCESS }
                if (allUserDataSuccess) {
                   userData = userDataResults[0].data!!
                    setUi(userData)
                } else {
                    // Handle errors
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProfileUpdated() {
       fetchData()
    }
}