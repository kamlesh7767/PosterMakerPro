package com.garudpuran.postermakerpro.data.interfaces

import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel

interface HomeRepo {
    suspend fun getAllFeedItems():List<FeedItem>
    suspend fun getAllTrendingStories():List<TrendingStoriesItemModel>
    suspend fun getAllCategoriesAndSubCategories():List<Pair<CategoryItem,List<SubCategoryItem>>>

    suspend fun getAllPosts(catId:String,subCatId:String):List<PostItem>
    suspend fun getAllRecharges():List<RechargeItem>

}