package com.garudpuran.postermakerpro.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentCreatePersonalProfileBinding
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.UserReferences
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint

class CreatePersonalProfileFragment(private val data: UserPersonalProfileModel,private val mListener:ProfileUpdateListener) :
    BottomSheetDialogFragment() {
    private lateinit var _binding: FragmentCreatePersonalProfileBinding
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
                imageUri =it
              startCrop(it)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePersonalProfileBinding.inflate(inflater, container, false)
        setAsShowed()
        setUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userProfilePic.setOnClickListener {
            profilePicsContract.launch("image/*")
        }

        binding.removeSelectedImageResetBtn.setOnClickListener {
            imageSelected = false
            if (data.profile_image_url.isNotEmpty()) {
                Glide.with(requireActivity()).load(data.profile_image_url)
                    .into(binding.userProfilePic)
            } else {
                binding.userProfilePic.setImageResource(R.drawable.pmp_image_placeholder)
            }
            binding.removeSelectedImageResetBtn.visibility = View.GONE
        }

        binding.updateUserProfileBtn.setOnClickListener {
            val userName = binding.registrationFullNameEt.text?.trim().toString()
            val mobile = binding.registrationMobileNoEt.text?.trim().toString()
            val userEmail = binding.registrationEmailEt.text?.trim().toString()
            val address = binding.registrationAddressEt.text?.trim().toString()

            if (userName.isNotEmpty()) {
                if (mobile.isNotEmpty()) {
                    if (userEmail.isNotEmpty()) {
                        if(address.isNotEmpty()){
                            createUserModel(userName, mobile, userEmail,address)
                        }else{
                            Utils.showToast(requireActivity(), getString(R.string.enter_your_address))
                        }

                    } else {
                        Utils.showToast(requireActivity(), getString(R.string.enter_your_email_id))
                    }
                } else {
                    Utils.showToast(requireActivity(), getString(R.string.enter_your_mobile_number))
                }
            } else {
                Utils.showToast(requireActivity(), getString(R.string.enter_your_name))
            }


        }


    }

    private fun createUserModel(userName: String, mobile: String, userEmail: String,address: String) {
        observeResponse()
        val profile = UserPersonalProfileModel(data.uid,userName,data.profile_image_url,mobile,address,userEmail,data.points,data.likedPosts,data.recharges)
        if (imageSelected) {
    userViewModel.updatePersonalProfileItem(imageUri.toString(),profile)
        } else {
            userViewModel.updatePersonalProfileItem("",profile)
        }


    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Set the desired aspect ratio
            .start(requireContext(), this, CROP_REQUEST_CODE)
    }

    private fun observeResponse() {
        userViewModel.onObserveUpdatePersonalProfileItemResponseData().observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progress.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    Utils.showToast(requireActivity(),"Error!")
                    binding.updateUserProfileBtn.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    if (it.data == ResponseStrings.SUCCESS) {
                        binding.progress.visibility = View.GONE
                        Utils.showToast(requireActivity(),"Updated Successfully")
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

    private fun setUI() {
        binding.registrationFullNameEt.setText(data.name)
        binding.registrationMobileNoEt.setText(data.mobile_number.removePrefix("+91"))
        binding.registrationEmailEt.setText(data.email)
        binding.registrationAddressEt.setText(data.address)
        if(data.profile_image_url.isNotEmpty()){
            Glide.with(requireActivity()).load(data.profile_image_url).into(binding.userProfilePic)
        }
    }

    private fun setAsShowed() {
        val sharedPreference = requireContext().getSharedPreferences(
            UserReferences.USER_PROFILE,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString(
            UserReferences.USER_PROFILE_STATUS,
            UserReferences.USER_PROFILE_STATUS_SHOWED
        )
        editor.apply()
    }
    interface ProfileUpdateListener {
        fun onProfileUpdated()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(data!!)
            imageUri = croppedUri!!
             binding.userProfilePic.setImageResource(R.drawable.naruto)
             binding.userProfilePic.setImageURI(croppedUri)
        }
    }


}