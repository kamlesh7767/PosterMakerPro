package com.garudpuran.postermakerpro.ui.editing.options

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.garudpuran.postermakerpro.databinding.FragmentOptionsFramesBinding
import com.garudpuran.postermakerpro.ui.editing.adapter.OptionFramesRcAdapter


class OptionsFramesFragment(private val mListener:OptionFramesListener) : Fragment(),
OptionFramesRcAdapter.OptionFramesRcAdapterListener{
    private var _binding: FragmentOptionsFramesBinding? = null
    private val binding get() = _binding!!
    // Store the RecyclerView state here
    private var recyclerViewState: Parcelable? = null
    private val recyclerViewTouchListener = View.OnTouchListener { _, event ->
        if (event.actionMasked == MotionEvent.ACTION_MOVE) {
            // Consume the touch event to prevent it from reaching the parent
            parentFragment?.view?.parent?.requestDisallowInterceptTouchEvent(true)
        }
        false
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsFramesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recycler_state")
        }

        val adapterOptions = OptionFramesRcAdapter(this, requireActivity())
        binding.editOptionsFramesRcView.adapter = adapterOptions
        binding.editOptionsFramesRcView.setOnTouchListener(recyclerViewTouchListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save RecyclerView state here
        recyclerViewState = binding.editOptionsFramesRcView.layoutManager?.onSaveInstanceState()
        outState.putParcelable("recycler_state", recyclerViewState)
    }
    override fun onOptionFramesClicked(position: Int) {
mListener.onFrameSelected(position)
    }

    interface OptionFramesListener{
        fun onFrameSelected(position: Int)

    }
}