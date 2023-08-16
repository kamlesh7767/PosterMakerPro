package com.garudpuran.postermakerpro.ui.commonui

import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel

class HomeResources {

    companion object{
        fun fullFrames():List<Int>{
            return listOf(R.layout.frame_0,R.layout.frame_1,R.layout.frame_2,R.layout.frame_3,R.layout.frame_4,R.layout.frame_5,R.layout.frame_6)
        }
        fun miniFrames():List<Int>{
            return listOf(R.layout.frame_0,R.drawable.mini_frame_one,R.drawable.mini_frame_two,R.drawable.mini_frame_three,R.drawable.mini_frame_four,R.drawable.mini_frame_five,R.drawable.mini_frame_six)
        }

        fun fonts():List<String>{
            return listOf("roboto_regular","roboto_light","roboto_semibold")
        }

    }

}