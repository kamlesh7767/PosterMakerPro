package com.garudpuran.postermakerpro.ui.commonui

import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel

class HomeResources {

    companion object{
        fun fullFrames():List<Int>{
            return listOf(R.layout.frame_0,R.layout.frame_1,R.layout.frame_2,R.layout.frame_3,R.layout.frame_4,R.layout.frame_5,R.layout.frame_6,R.layout.frame_7,R.layout.frame_8,R.layout.frame_9,R.layout.frame_10,R.layout.frame_11,R.layout.frame_12,R.layout.frame_13,R.layout.frame_14,R.layout.frame_15,R.layout.frame_16,R.layout.frame_17,R.layout.frame_18,R.layout.frame_19)
        }
        fun miniFrames():List<Int>{
            return listOf(R.layout.frame_0,R.drawable.mini_frame_one,R.drawable.mini_frame_two,R.drawable.mini_frame_three,R.drawable.mini_frame_four,R.drawable.mini_frame_five,R.drawable.mini_frame_six,R.drawable.mini_frame_seven,R.drawable.mini_frame_eight,R.drawable.mini_frame_nine,R.drawable.mini_frame_ten,R.drawable.mini_frame_elleven,R.drawable.mini_frame_twelve,R.drawable.mini_frame_thirteen,R.drawable.mini_frame_fourteen,R.drawable.mini_frame_fifteen,R.drawable.mini_frame_sixteen,R.drawable.mini_frame_seventeen,R.drawable.mini_frame_eighteen,R.drawable.mini_frame_ninteen)
        }

        fun fonts():List<String>{
            return listOf("roboto_regular","roboto_light","roboto_semibold")
        }

    }

}