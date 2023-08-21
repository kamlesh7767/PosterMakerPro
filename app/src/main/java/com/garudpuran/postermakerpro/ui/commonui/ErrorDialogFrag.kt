package com.garudpuran.postermakerpro.ui.commonui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityMainBinding
import com.garudpuran.postermakerpro.databinding.FragmentDownloadAndShareCustomDialogBinding
import com.garudpuran.postermakerpro.databinding.FragmentErrorDialogBinding
import com.garudpuran.postermakerpro.databinding.FragmentLanguageSelectionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ErrorDialogFrag(private val mListener:ErrorDialogListener) : DialogFragment() {

    private var _binding: FragmentErrorDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentErrorDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeDialog.setOnClickListener {
            dismiss()
        }

        binding.dialogRetryBtn.setOnClickListener {
           dismiss()
        }


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mListener.onDialogDismissed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false) // Disable cancellation on outside touch
        dialog.setCancelable(false) // Disable cancellation on back press
        return dialog
    }

    interface ErrorDialogListener{
        fun onDialogDismissed()
    }
}
