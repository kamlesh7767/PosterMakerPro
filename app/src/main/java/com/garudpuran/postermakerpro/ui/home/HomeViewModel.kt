package com.garudpuran.postermakerpro.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garudpuran.postermakerpro.data.interfaces.HomeRepo
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.models.SearchModel
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainVMI: HomeRepo
) : ViewModel() {
    private val trendingStoriesCache = MutableLiveData<List<TrendingStoriesItemModel>>()
    private val feedItemsCache = MutableLiveData<List<FeedItem>>()
    private val catSubCatCache = MutableLiveData<List<Pair<CategoryItem,List<SubCategoryItem>>>>()
    fun setTrendingStoriesCache(data: List<TrendingStoriesItemModel>) {
        trendingStoriesCache.value = data
    }
    fun getTrendingStoriesCache(): LiveData<List<TrendingStoriesItemModel>> {
        return trendingStoriesCache
    }

    fun setFeedItemsCacheCache(data: List<FeedItem>) {
        feedItemsCache.value = data
    }
    fun getFeedItemsCacheCache(): LiveData<List<FeedItem>> {
        return feedItemsCache
    }

    fun setCatSubCatCacheCache(data: List<Pair<CategoryItem,List<SubCategoryItem>>>) {
        catSubCatCache.value = data
    }
    fun getCatSubCatCacheCache(): LiveData<List<Pair<CategoryItem,List<SubCategoryItem>>>> {
        return catSubCatCache
    }

    //getAllFeedItems

    suspend fun getAllFeedItemsAsync(): Resource<List<FeedItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val uc = mainVMI.getAllFeedItems()
                if (uc.isNotEmpty()) {
                    Resource.success(uc)
                } else {
                    Resource.error(emptyList())
                }
            } catch (e: Exception) {
                Resource.error(emptyList())
            }
        }
    }


    //GetAllTrendingStories
    suspend fun getAllTrendingStoriesAsync(): Resource<List<TrendingStoriesItemModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val uc = mainVMI.getAllTrendingStories()
                if (uc.isNotEmpty()) {
                    Resource.success(uc)
                } else {
                    Resource.error(emptyList())
                }
            } catch (e: Exception) {
                Resource.error(emptyList())
            }
        }
    }


    //GetAllCategoriesAndSubCategories
     suspend fun getAllCategoriesAndSubCategoriesAsync(): Resource<List<Pair<CategoryItem,List<SubCategoryItem>>>> {
        return withContext(Dispatchers.IO) {
            try {
                val uc = mainVMI.getAllCategoriesAndSubCategories()
                if (uc.isNotEmpty()) {
                    Resource.success(uc)
                } else {
                    Resource.error(emptyList())
                }
            } catch (e: Exception) {
                Resource.error(emptyList())
            }
        }
    }




    //getAllPosts
    private val getAllPostsResponse =  MutableLiveData<Resource<List<PostItem>>>()

    fun getAllPosts(catId:String,subCatId:String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAllPostsResponse.postValue(Resource.loading(emptyList()))
            val uc   = mainVMI.getAllPosts(catId,subCatId)
            if(uc.isNotEmpty()){
                getAllPostsResponse.postValue(Resource.success(uc))
            }else{
                getAllPostsResponse.postValue(Resource.error(emptyList()))
            }

        }
    }
    fun onObserveGetAllPostsResponseData(): LiveData<Resource<List<PostItem>>> {
        return getAllPostsResponse
    }

    //getAllRcg
    private val getAllRcgResponse =  MutableLiveData<Resource<List<RechargeItem>>>()

    fun getAllRcg() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllRcgResponse.postValue(Resource.loading(emptyList()))
            val uc   = mainVMI.getAllRecharges()
            if(uc.isNotEmpty()){
                getAllRcgResponse.postValue(Resource.success(uc))
            }else{
                getAllRcgResponse.postValue(Resource.error(emptyList()))
            }

        }
    }
    fun onObserveGetAllRcgResponseData(): LiveData<Resource<List<RechargeItem>>> {
        return getAllRcgResponse
    }


// getAllSearchItems
    suspend fun getAllSearchItems(): Resource<List<SearchModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val uc = mainVMI.getAllSearchItems()
                if (uc.isNotEmpty()) {
                    Resource.success(uc)
                } else {
                    Resource.error(emptyList())
                }
            } catch (e: Exception) {
                Resource.error(emptyList())
            }
        }
    }

    private val allSearchItemsCache = MutableLiveData< List<SearchModel>>()
    fun setAllSearchItemsCache(data: List<SearchModel>) {
        allSearchItemsCache.value = data
    }
    fun getAllSearchItemsCache(): LiveData<List<SearchModel>> {
        return allSearchItemsCache
    }


}