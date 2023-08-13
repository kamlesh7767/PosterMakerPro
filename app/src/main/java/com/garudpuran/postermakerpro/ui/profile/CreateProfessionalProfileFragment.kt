package com.garudpuran.postermakerpro.ui.profile

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class CreateProfessionalProfileFragment(private val mListener: ProProfileUpdateListener) :
    BottomSheetDialogFragment() {
    private lateinit var _binding: FragmentCreateProfessionalProfileBinding
    private val binding get() = _binding
    private lateinit var imageUri: Uri
    private val userViewModel: UserViewModel by viewModels()
    private var imageSelected = false
    private val profilePicsContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                imageSelected = true
                binding.removeSelectedImageResetBtn.visibility = View.VISIBLE
                imageUri = it
                binding.userProfilePic.setImageURI(it)
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
        _binding = FragmentCreateProfessionalProfileBinding.inflate(inflater, container, false)
        setAsShowed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editProfilePicBtn.setOnClickListener {
            profilePicsContract.launch("image/*")
        }

        binding.updateUserProfileBtn.setOnClickListener {
            val userName = binding.registrationFullNameEt.text?.trim().toString()
            val mobile = binding.registrationMobileNoEt.text?.trim().toString()
            val userAddress = binding.registrationAddressEt.text?.trim().toString()

            if (userName.isNotEmpty()) {
                if (mobile.isNotEmpty()) {
                    if (userAddress.isNotEmpty()) {
                        createUserModel(userName, mobile, userAddress)
                    } else {
                        Utils.showToast(requireActivity(), "Enter your address.")
                    }
                } else {
                    Utils.showToast(requireActivity(), "Enter your mobile number.")
                }
            } else {
                Utils.showToast(requireActivity(), "Enter your name.")
            }


        }
    }

    private fun createUserModel(userName: String, mobile: String, userAddress: String) {
        observeResponse()
        val profile = UserProfessionalProfileModel("", userName, mobile, userAddress, "")
        if (imageSelected) {
            userViewModel.updateProfessionalProfileItem(imageUri.toString(), profile)
        } else {
            Utils.showToast(requireActivity(), "Please select logo image")
        }


    }

    private fun observeResponse() {
        userViewModel.onObserveUpdateProfessionalProfileItemResponseData()
            .observe(requireActivity()) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progress.root.visibility = View.VISIBLE
                    }

                    Status.ERROR -> {
                        Utils.showToast(requireActivity(), "Error!")
                        binding.updateUserProfileBtn.visibility = View.VISIBLE
                        binding.progress.root.visibility = View.GONE
                    }

                    Status.SUCCESS -> {
                        if (it.data == ResponseStrings.SUCCESS) {
                            binding.progress.root.visibility = View.GONE
                            Utils.showToast(requireActivity(), "Updated Successfully")
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        return dialog
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

    interface ProProfileUpdateListener {
        fun onProfileUpdated()

    }


}