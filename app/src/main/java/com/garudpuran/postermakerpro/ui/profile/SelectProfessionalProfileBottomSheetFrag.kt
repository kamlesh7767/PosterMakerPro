package com.garudpuran.postermakerpro.ui.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentSelectProfessionalProfileBottomSheetBinding
import com.garudpuran.postermakerpro.models.UserProfessionalProfileModel
import com.garudpuran.postermakerpro.ui.editing.EditPostActivity
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@AndroidEntryPoint

class SelectProfessionalProfileBottomSheetFrag(
    private val postImageUrl: String,
    private val postEngTitle: String,
    private val postMarTitle: String,
    private val postHinTitle: String,
    private val  categoryId: String,
    private val  subCategoryId: String,
    private val postId: String
) :
    BottomSheetDialogFragment(), SelectProfileAdapter.SelectProfileAdapterListener {
    private lateinit var _binding: FragmentSelectProfessionalProfileBottomSheetBinding
    private val binding get() = _binding

    private val userViewModel: UserViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectProfessionalProfileBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserData()

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
        val adapter = SelectProfileAdapter(this)
        adapter.setData(data)
        binding.selectProfileItems.adapter = adapter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        return dialog
    }

    override fun onSelectProfileAdapterItemClicked(item: UserProfessionalProfileModel) {
        val intent = Intent(requireActivity(), EditPostActivity::class.java)
        intent.putExtra("imageUrl",postImageUrl)
        intent.putExtra("engTitle",postEngTitle)
        intent.putExtra("marTitle",postMarTitle)
        intent.putExtra("hinTitle",postHinTitle)

        intent.putExtra("postCatId",categoryId)
        intent.putExtra("postSubCatId",subCategoryId)
        intent.putExtra("postId",postId)

        intent.putExtra("profileName",item.name)
        intent.putExtra("profileMobileNumber",item.mobile_number)
        intent.putExtra("profileAddress",item.address)
        intent.putExtra("profileLogoUrl",item.logo_image_url)
        startActivity(intent)
        dismiss()
    }


}