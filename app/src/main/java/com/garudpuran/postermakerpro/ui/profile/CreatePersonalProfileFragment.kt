package com.garudpuran.postermakerpro.ui.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentCreatePersonalProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CreatePersonalProfileFragment() : BottomSheetDialogFragment()  {
    private lateinit var _binding:FragmentCreatePersonalProfileBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePersonalProfileBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        return dialog

    }


}