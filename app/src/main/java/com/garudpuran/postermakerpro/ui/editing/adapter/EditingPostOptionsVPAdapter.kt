package com.garudpuran.postermakerpro.ui.editing.adapter

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.garudpuran.postermakerpro.ui.editing.options.OptionsAddressFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsFramesFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsLogoFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsMobileNumberFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsProfilePhotoFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsUserNameFragment


class EditingPostOptionsVPAdapter(fragment: Fragment?) : FragmentStateAdapter(fragment!!) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OptionsFramesFragment()
            1 -> OptionsProfilePhotoFragment()
            2 -> OptionsLogoFragment()
            3 -> OptionsUserNameFragment()
            4 -> OptionsAddressFragment()
            5 -> OptionsMobileNumberFragment()

            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

}
