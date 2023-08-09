package com.garudpuran.postermakerpro.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garudpuran.postermakerpro.data.interfaces.HomeRepo
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainVMI: HomeRepo
) : ViewModel() {

    //getAllFeedItems
    private val getAllFeedItemsResponse =  MutableLiveData<Resource<List<FeedItem>>>()

    fun getAllFeedItems() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllFeedItemsResponse.postValue(Resource.loading(emptyList()))
            val uc   = mainVMI.getAllFeedItems()
            if(uc.isNotEmpty()){
                getAllFeedItemsResponse.postValue(Resource.success(uc))
            }else{
                getAllFeedItemsResponse.postValue(Resource.error(emptyList()))
            }

        }
    }
    fun onObserveGetAllFeedItemsResponseData(): LiveData<Resource<List<FeedItem>>> {
        return getAllFeedItemsResponse
    }

    //GetAllTrendingStories
    private val getAllTrendingStoriesResponse =  MutableLiveData<Resource<List<TrendingStoriesItemModel>>>()

    fun getAllTrendingStories() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllTrendingStoriesResponse.postValue(Resource.loading(emptyList()))
            val uc   = mainVMI.getAllTrendingStories()
            if(uc.isNotEmpty()){
                getAllTrendingStoriesResponse.postValue(Resource.success(uc))
            }else{
                getAllTrendingStoriesResponse.postValue(Resource.error(emptyList()))
            }

        }
    }
    fun onObserveGetAllTrendingStoriesResponseData(): LiveData<Resource<List<TrendingStoriesItemModel>>> {
        return getAllTrendingStoriesResponse
    }


    //GetAllCategoriesAndSubCategories
    private val getAllCategoriesAndSubCategoriesResponse =  MutableLiveData<Resource<List<Pair<CategoryItem,List<SubCategoryItem>>>>>()

    fun getAllCategoriesAndSubCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoriesAndSubCategoriesResponse.postValue(Resource.loading(emptyList()))
            val uc   = mainVMI.getAllCategoriesAndSubCategories()
            if(uc.isNotEmpty()){
                getAllCategoriesAndSubCategoriesResponse.postValue(Resource.success(uc))
            }else{
                getAllCategoriesAndSubCategoriesResponse.postValue(Resource.error(emptyList()))
            }

        }
    }
    fun onObserveGetAllCategoriesAndSubCategoriesResponseData(): LiveData<Resource<List<Pair<CategoryItem,List<SubCategoryItem>>>>> {
        return getAllCategoriesAndSubCategoriesResponse
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


}