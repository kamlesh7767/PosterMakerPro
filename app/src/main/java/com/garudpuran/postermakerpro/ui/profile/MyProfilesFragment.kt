package com.garudpuran.postermakerpro.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentMyProfilesBinding
import com.garudpuran.postermakerpro.models.UserProfessionalProfileModel
import com.garudpuran.postermakerpro.ui.authentication.PhoneActivity
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.UserReferences
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfilesFragment : Fragment(), ProfileItemsAdapter.ProfileItemsAdapterListener,
    CreateProfessionalProfileFragment.ProProfileUpdateListener {
    private lateinit var _binding: FragmentMyProfilesBinding
    private val binding get() = _binding

    private val userViewModel: UserViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
observeUserData()
        binding.createProfProfileBtn.setOnClickListener {
           val frag = CreateProfessionalProfileFragment(this)
            frag.show(childFragmentManager,"CreateProfessionalProfileFragment")
        }

    }


    private fun observeUserData() {
        val userProfilesCache = userViewModel.getAllProfessionalProfileItemsCache()
        if (userProfilesCache.value == null) {
            fetchData()
        }else{
            setUi(userProfilesCache.value!!)
        }
    }
    private fun fetchData() {
        this.lifecycleScope.launch {
            try {
                val trendingStoriesDeferred4 =
                    async { userViewModel.getAllProfessionalProfileItemsAsync() }

                val userDataResults = awaitAll(
                    trendingStoriesDeferred4
                )

                // Check results and proceed
                val allUserDataSuccess = userDataResults.all { it.status == Status.SUCCESS }
                if (allUserDataSuccess) {
                    userViewModel.setAllProfessionalProfileItemsCache(userDataResults[0].data!!)
                    setUi(userDataResults[0].data!!)
                } else {
                    // Handle errors
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun setUi(data: List<UserProfessionalProfileModel>) {
        if(data.isEmpty()){
            binding.noProfilesLl.visibility = View.VISIBLE
        }else{
            val adapter = ProfileItemsAdapter(this)
            adapter.setData(data)
            binding.myProfileItems.adapter = adapter
            binding.noProfilesLl.visibility = View.GONE
        }

    }

    override fun onProfileDeleteBtnClicked(item: UserProfessionalProfileModel) {

        val builder =
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.are_you_sure))
                .setMessage(getString(R.string.profile_will_be_deleted))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { _, _ ->
                   FirebaseFirestore.getInstance().collection(UserReferences.USER_MAIN_NODE).document(auth.uid!!).collection(UserReferences.USER_PROFESSIONAL_PROFILES).document(item.id!!).delete().addOnSuccessListener {
                        Utils.showToast(requireActivity(),getString(R.string.profile_deleted))
                        fetchData()
                    }
                }
                .setNegativeButton(getString(R.string.no), null)
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onProfileUpdated() {
        fetchData()
    }

}