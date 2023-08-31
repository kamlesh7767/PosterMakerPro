package com.garudpuran.postermakerpro.ui.editing.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.garudpuran.postermakerpro.ui.editing.EditPostActivity
import com.garudpuran.postermakerpro.ui.editing.options.OptionsAddressFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsFramesFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsLogoFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsMobileNumberFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsProfilePhotoFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsUserNameFragment


class EditingPostOptionsVPAdapter(private val optionsFrameListener: OptionsFramesFragment.OptionFramesListener,
                                  private val optionsProfileListener: OptionsProfilePhotoFragment.OptionsProfilePicListener,
                                  private val optionsLogoListener: OptionsLogoFragment.OptionsLogoListener,
                                  private val optionsNameListener: OptionsUserNameFragment.OptionsNameListener,
                                  private val optionsAddressListener: OptionsAddressFragment.OptionsAddressListener,
                                  private val optionsMoNoListener: OptionsMobileNumberFragment.OptionsMoNoListener,
                                  activity: EditPostActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OptionsFramesFragment(optionsFrameListener)
            1 -> OptionsProfilePhotoFragment(optionsProfileListener)
            2 -> OptionsLogoFragment(optionsLogoListener)
            3 -> OptionsUserNameFragment(optionsNameListener)
            4 -> OptionsAddressFragment(optionsAddressListener)
            5 -> OptionsMobileNumberFragment(optionsMoNoListener)

            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }


    override fun getItemCount(): Int {
        return 6
    }

}
