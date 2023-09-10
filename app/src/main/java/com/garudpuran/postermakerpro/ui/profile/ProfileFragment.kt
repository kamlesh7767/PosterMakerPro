package com.garudpuran.postermakerpro.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentProfileBinding
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.authentication.PhoneActivity
import com.garudpuran.postermakerpro.ui.commonui.ContactUsDialog
import com.garudpuran.postermakerpro.ui.commonui.ErrorDialogFrag
import com.garudpuran.postermakerpro.ui.commonui.LanguageSelectionBottomSheetFragment
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class ProfileFragment : Fragment(),CreatePersonalProfileFragment.ProfileUpdateListener,LanguageSelectionBottomSheetFragment.LanguageSelectionListener,
    ErrorDialogFrag.ErrorDialogListener, ContactUsDialog.ContactUsDialogListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var userData = UserPersonalProfileModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
              startActivity(requireActivity().intent)
                requireActivity().finish()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

observeUserData()

        showAd()
        return binding.root
    }

    private fun showAd() {
        val adRequest = AdRequest.Builder().build()
        binding.profileBannerAdV.loadAd(adRequest)
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

        binding.profilePrivacyPolicyBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToWebViewFragment()
            findNavController().navigate(action)
        }

        binding.profileContactUsBtn.setOnClickListener {
         contactFrag()
        }

        binding.profileDeleteAccBtn.setOnClickListener {
          contactFrag()
        }


        binding.profileBack.setOnClickListener {
            startActivity(requireActivity().intent)
        }

        binding.profileLanguageBtn.setOnClickListener {
            val action = LanguageSelectionBottomSheetFragment(false,this)
            action.show(childFragmentManager,"")
        }

        binding.profileRechargeBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToRechargesFragment()
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

        binding.profileFragUserPicCiv.setOnClickListener {
        binding.editPersonalProfileBtn.performClick()
        }

        binding.profileSignOutBtn.setOnClickListener {
            val builder =
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.you_will_be_signed_out))
                    .setCancelable(true)
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { _, _ ->
                        auth.signOut()
                        val intent = Intent(requireActivity(),PhoneActivity().javaClass)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .setNegativeButton(getString(R.string.no), null)
            val alertDialog = builder.create()
            alertDialog.show()

        }
    }

    private fun contactFrag(){
        val contactFrag = ContactUsDialog(this)
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.add(contactFrag, "ContactDialogFrag")
        fragmentTransaction.commitAllowingStateLoss()
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

        binding.profileWalletPointsTv.text = data.points.toString()

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
                    setErrorDialog()
                }
            } catch (e: Exception) {
                // Handle exceptions
                setErrorDialog()
            }
        }
    }

    private fun setErrorDialog() {
        val errorD = ErrorDialogFrag(this)
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.add(errorD, "ErrorDialogFrag")
        fragmentTransaction.commitAllowingStateLoss()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProfileUpdated() {
       fetchData()
    }

    override fun onLanguageSelected(language: Int) {
        var lang: String
        if(language == 1){
            setAppLocale(requireActivity(),"en")
            lang = "en"
        }
        else if(language == 2){
            setAppLocale(requireActivity(),"hi")
            lang = "hi"
        }

        else{
            setAppLocale(requireActivity(),"mr")
            lang = "mr"
        }

        val sharedPreference = requireActivity().getSharedPreferences(
            AppPrefConstants.LANGUAGE_PREF,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString("language", lang)
        editor.apply()
        requireActivity().finish()
        startActivity(requireActivity().intent)
    }

    private fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    override fun onDialogDismissed() {
        startActivity(requireActivity().intent)
    }

    override fun onCreateMailClicked() {
        composeEmail("postermakerpro@gmail.com")
    }

    override fun onCopyMailClicked() {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val textToCopy = "postermakerpro@gmail.com"
        val clip = ClipData.newPlainText("Label", textToCopy)
        clipboard.setPrimaryClip(clip)
        Utils.showToast(requireActivity(),"Copied")
    }

    private fun composeEmail(emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")  // Specifies that this is a mailto: intent
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            onCopyMailClicked()
        }
    }
}