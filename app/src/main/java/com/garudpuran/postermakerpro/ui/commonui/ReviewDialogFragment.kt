package com.garudpuran.postermakerpro.ui.commonui

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
import androidx.fragment.app.DialogFragment
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentReviewDialogBinding
import com.garudpuran.postermakerpro.utils.UserReferences


class ReviewDialogFragment : DialogFragment(){
    private var _binding: FragmentReviewDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReviewDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeDialog.setOnClickListener {
            dismiss()
        }

        binding.rateAppBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.garudpuran.postermakerpro")
            startActivity(intent)
        }

        binding.doNotShowCheck.setOnCheckedChangeListener { p0, p1 ->
            if(p1){
                setAsDoNotShow()
            }else{
                setAsShow()
            }

        }

    }

    private fun setAsShow() {
        val sharedPreference = requireContext().getSharedPreferences(
            UserReferences.USER_PROFILE,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString(
            UserReferences.USER_REVIEW_STATUS_SHOW,
            UserReferences.USER_REVIEW_STATUS_DO_SHOW
        )
        editor.apply()

    }

    private fun setAsDoNotShow() {
        val sharedPreference = requireContext().getSharedPreferences(
            UserReferences.USER_PROFILE,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString(
            UserReferences.USER_REVIEW_STATUS_SHOW,
            UserReferences.USER_REVIEW_STATUS_DO_NOT
        )
        editor.apply()
    }


}