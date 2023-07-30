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
    }

}