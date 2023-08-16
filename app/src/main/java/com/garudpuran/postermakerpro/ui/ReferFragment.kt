package com.garudpuran.postermakerpro.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentProfileBinding
import com.garudpuran.postermakerpro.databinding.FragmentReferBinding
import com.garudpuran.postermakerpro.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReferFragment : Fragment() {
    private var _binding: FragmentReferBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentReferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.referCode.text = generateRandomCode(8)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.referButton.setOnClickListener {
            // Predefined text message
            val message = "Good Morning and many more Post to send wishes and celebrate and make the day more special. download app now below link.\n" +
                    "\n" +
                    "Refer code - QQ22BKWH\n"

            val playStoreUrl = "https://play.google.com/store/apps/details?id=com.vyroai.aiart"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "$message\n\n$playStoreUrl")
            if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
        }

        binding.copyCode.setOnClickListener {
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val textToCopy = binding.referCode.text
            val clip = ClipData.newPlainText("Label", textToCopy)
            clipboard.setPrimaryClip(clip)
            Utils.showToast(requireActivity(),"Copied")
        }


    }

    fun generateRandomCode(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }


}