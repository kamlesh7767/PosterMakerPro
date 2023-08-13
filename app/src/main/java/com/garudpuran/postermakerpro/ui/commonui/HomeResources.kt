package com.garudpuran.postermakerpro.ui.commonui

import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel

class HomeResources {

    companion object{
        fun homeCategories():List<HomeCategoryModel>{
            return listOf(
                HomeCategoryModel(1, R.string.cat_political_title,R.drawable.political),
                HomeCategoryModel(2, R.string.cat_festival_title,R.drawable.festival),
                HomeCategoryModel(3, R.string.cat_quotes_title,R.drawable.quotes),
                HomeCategoryModel(4, R.string.cat_business_title,R.drawable.business),
                HomeCategoryModel(5, R.string.cat_trending_title,R.drawable.trending),
                HomeCategoryModel(6, R.string.cat_daily_news_title,R.drawable.daily_news),
                HomeCategoryModel(7, R.string.cat_logo_design_title,R.drawable.logo_design),
                HomeCategoryModel(8, R.string.cat_other_title,R.drawable.other)
            )
        }

        fun fullFrames():List<Int>{
            return listOf(R.layout.frame_0,R.layout.frame_1,R.layout.frame_2,R.layout.frame_3,R.layout.frame_4,R.layout.frame_5,R.layout.frame_6)
        }
        fun miniFrames():List<Int>{
            return listOf(R.layout.frame_0,R.drawable.mini_frame_one,R.drawable.mini_frame_two,R.drawable.mini_frame_three,R.drawable.mini_frame_four,R.drawable.mini_frame_five,R.drawable.mini_frame_six)
        }

        fun fonts():List<String>{
            return listOf("roboto_light","roboto_regular","roboto_semibold")
        }

    }

}