package com.garudpuran.postermakerpro.ui.editing.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentOptionsFramesBinding
import com.garudpuran.postermakerpro.ui.commonui.models.EditFragOptionsModel
import com.garudpuran.postermakerpro.ui.editing.EditPostActivity
import com.garudpuran.postermakerpro.ui.editing.adapter.EditFragOptionsAdapter


class OptionsFramesFragment : Fragment(), EditFragOptionsAdapter.EditOptionsListener {
    private var _binding: FragmentOptionsFramesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOptionsFramesBinding.inflate(inflater, container, false)
        val adapterOptions = EditFragOptionsAdapter(requireActivity() as EditPostActivity, this)
        binding.editOptionsFramesRcView.adapter = adapterOptions
        return binding.root
    }

    override fun onEditOptionsClicked(item: EditFragOptionsModel) {

    }
}