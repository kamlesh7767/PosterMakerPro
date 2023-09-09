package com.garudpuran.postermakerpro.ui.commonui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentLanguageSelectionBottomSheetBinding
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class LanguageSelectionBottomSheetFragment(val autoSet:Boolean, private val listener: LanguageSelectionListener) : BottomSheetDialogFragment() {
    private var _binding: FragmentLanguageSelectionBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLanguageSelectionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false) // Disable cancellation on outside touch
        dialog.setCancelable(false) // Disable cancellation on back press
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedLang =  getSelectedLanguage()

        if(selectedLang == "mr"){
            binding.radioButtonMarathi.isChecked = true
        }else if(selectedLang == "hi"){
            binding.radioButtonHindi.isChecked = true
        }else{
            binding.radioButtonEnglish.isChecked = true
        }

        binding.okButton.setOnClickListener {
            if(binding.radioButtonEnglish.isChecked){
                listener.onLanguageSelected(1)
            }
            else if(binding.radioButtonHindi.isChecked){
                listener.onLanguageSelected(2)
            }

            else{
                listener.onLanguageSelected(3)
            }
            dismiss()

        }

    }

    private fun getSelectedLanguage(): String {
        val authPref = requireActivity().getSharedPreferences(AppPrefConstants.LANGUAGE_PREF, Context.MODE_PRIVATE)
        return authPref.getString("language", "")!!
    }

    interface LanguageSelectionListener {
        fun onLanguageSelected(language: Int)
    }

    override fun onCancel(dialog: DialogInterface) {
        if(autoSet){
            listener.onLanguageSelected(2)
        }

    }


}