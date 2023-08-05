package com.garudpuran.postermakerpro.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garudpuran.postermakerpro.data.interfaces.HomeRepo
import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.models.FeedItem
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

    //likefeedItem
    private val likeFeedItemResponse = MutableLiveData<Resource<String>>()

    fun likeFeedItem(item: FeedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            likeFeedItemResponse.postValue(Resource.loading(null))
            val uc   = mainVMI.likeFeedItem(item)
            if(uc != null){
                likeFeedItemResponse.postValue(Resource.success(uc))

            }else{
                likeFeedItemResponse.postValue(Resource.error(null))

            }

        }
    }
    fun onObserveLikeFeedItemResponseData(): LiveData<Resource<String>> {
        return likeFeedItemResponse
    }


}