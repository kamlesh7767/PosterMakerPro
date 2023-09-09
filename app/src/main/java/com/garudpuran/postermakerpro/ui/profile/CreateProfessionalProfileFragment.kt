package com.garudpuran.postermakerpro.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentCreatePersonalProfileBinding
import com.garudpuran.postermakerpro.databinding.FragmentCreateProfessionalProfileBinding
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.models.UserProfessionalProfileModel
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.UserReferences
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint

class CreateProfessionalProfileFragment(private val mListener: ProProfileUpdateListener) :
    BottomSheetDialogFragment() {
    private lateinit var _binding: FragmentCreateProfessionalProfileBinding
    private val binding get() = _binding
    private lateinit var imageUri: Uri
    private val CROP_REQUEST_CODE = 101
    private val userViewModel: UserViewModel by viewModels()
    private var imageSelected = false
    private val profilePicsContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                imageSelected = true
                binding.removeSelectedImageResetBtn.visibility = View.VISIBLE
                imageUri = it
                startCrop(it)
            }
        }
    private fun showAd() {
        val adRequest = AdRequest.Builder().build()
        binding.profileBannerAdV.loadAd(adRequest)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProfessionalProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAd()
        binding.userProfilePic.setOnClickListener {
            profilePicsContract.launch("image/*")
        }

        binding.updateUserProfileBtn.setOnClickListener {
            val userName = binding.registrationFullNameEt.text?.trim().toString()
            val mobile = binding.registrationMobileNoEt.text?.trim().toString()
            val userAddress = binding.registrationAddressEt.text?.trim().toString()

            if (userName.isNotEmpty()) {
                if (mobile.isNotEmpty()&&mobile.length==10) {
                    if (userAddress.isNotEmpty()) {
                        createUserModel(userName, mobile, userAddress)
                    } else {
                        Utils.showToast(requireActivity(), getString(R.string.enter_your_address))
                    }
                } else {
                    Utils.showToast(requireActivity(), getString(R.string.enter_your_mobile_no))
                }
            } else {
                Utils.showToast(requireActivity(), getString(R.string.enter_your_name))
            }


        }

        binding.removeSelectedImageResetBtn.setOnClickListener {
            imageSelected = false
            binding.userProfilePic.setImageResource(R.drawable.naruto)
            binding.removeSelectedImageResetBtn.visibility = View.GONE
        }


        binding.registrationMobileNoEt.addTextChangedListener {
            val ss = it?.removePrefix("+91")
        }
    }

    private fun createUserModel(userName: String, mobile: String, userAddress: String) {
        observeResponse()
        val profile = UserProfessionalProfileModel("", userName, mobile, userAddress, "")
        if (imageSelected) {
            userViewModel.updateProfessionalProfileItem(imageUri.toString(), profile)
        } else {
            Utils.showToast(requireActivity(), getString(R.string.please_select_logo_image))
        }


    }

    private fun observeResponse() {
        userViewModel.onObserveUpdateProfessionalProfileItemResponseData()
            .observe(requireActivity()) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progress.visibility = View.VISIBLE
                    }

                    Status.ERROR -> {
                        Utils.showToast(requireActivity(), getString(R.string.error))
                        binding.updateUserProfileBtn.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                    }

                    Status.SUCCESS -> {
                        if (it.data == ResponseStrings.SUCCESS) {
                            binding.progress.visibility = View.GONE
                            Utils.showToast(requireActivity(), getString(R.string.updated_successfully))
                            binding.updateUserProfileBtn.visibility = View.VISIBLE
                            mListener.onProfileUpdated()
                            dismiss()
                        }
                    }

                    Status.SESSION_EXPIRE -> {

                    }
                }
            }
    }

    interface ProProfileUpdateListener {
        fun onProfileUpdated()

    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Set the desired aspect ratio
            .start(requireContext(), this, CROP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(data!!)
            imageUri = croppedUri!!
            binding.userProfilePic.setImageResource(R.drawable.pmp_image_placeholder)
            binding.userProfilePic.setImageURI(croppedUri)
        }
    }
}