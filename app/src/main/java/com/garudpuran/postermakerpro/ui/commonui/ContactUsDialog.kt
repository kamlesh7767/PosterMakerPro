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
import com.garudpuran.postermakerpro.databinding.FragmentContactUsDialogBinding
import com.garudpuran.postermakerpro.databinding.FragmentDownloadAndShareCustomDialogBinding
import com.garudpuran.postermakerpro.databinding.FragmentErrorDialogBinding
import com.garudpuran.postermakerpro.databinding.FragmentLanguageSelectionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ContactUsDialog(private val mListener:ContactUsDialogListener) : DialogFragment() {

    private var _binding: FragmentContactUsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactUsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeDialog.setOnClickListener {
            dismiss()
        }

        binding.dialogSendMailBtn.setOnClickListener {
            mListener.onCreateMailClicked()
            dismiss()
        }


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

    interface ContactUsDialogListener{
        fun onCreateMailClicked()
    }
}
