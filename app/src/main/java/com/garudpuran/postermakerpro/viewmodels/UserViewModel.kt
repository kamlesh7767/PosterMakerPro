package com.garudpuran.postermakerpro.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.models.UserProfessionalProfileModel
import com.garudpuran.postermakerpro.utils.Resource
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val mainVMI: UserViewModelVMI
) : ViewModel() {

    //updateUserDetails
    private val updateUserDetailsData = MutableLiveData<Resource<String>>()

    fun updateUserDetails(id:String,userModel: UserPersonalProfileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserDetailsData.postValue(Resource.loading(null))
            val uc   = mainVMI.updateProfile(id,userModel)
            if(uc == ResponseStrings.SUCCESS){
                updateUserDetailsData.postValue(Resource.success(uc))
            }else{
                updateUserDetailsData.postValue(Resource.error(null))

            }

        }
    }
    fun onObserveUpdateUserDetailsData(): LiveData<Resource<String>> {
        return updateUserDetailsData
    }


    //uploadFeedPostItem
    private val uploadFeedPostItemResponse = MutableLiveData<Resource<String>>()

    fun uploadFeedPostItem(imageUri: Uri?, item:FeedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            uploadFeedPostItemResponse.postValue(Resource.loading(null))
            val uc   = mainVMI.uploadFeedPostItem(imageUri,item)
            if(uc != null){
                uploadFeedPostItemResponse.postValue(Resource.success(uc))

            }else{
                uploadFeedPostItemResponse.postValue(Resource.error(null))

            }

        }
    }
    fun onObserveUploadFeedPostItemResponseData(): LiveData<Resource<String>> {
        return uploadFeedPostItemResponse
    }

    //updateUserDetailsParams
    private val updateUserDetailsDataParams = MutableLiveData<Resource<String>>()

    fun updateUserDetailsParams(id:String,params: Map<String,Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserDetailsDataParams.postValue(Resource.loading(null))
            val uc   = mainVMI.updateProfileFields(id,params)
            Log.d("RESPONSE_DATA2",uc)
            if(uc == ResponseStrings.SUCCESS){
                updateUserDetailsDataParams.postValue(Resource.success(uc))
                Log.d("RESPONSE_DATA3",uc)
            }else{
                updateUserDetailsDataParams.postValue(Resource.error(null))
                Log.d("RESPONSE_DATA4",uc)
            }

        }
    }
    fun onObserveUpdateUserDetailsParamsData(): LiveData<Resource<String>> {
        return updateUserDetailsDataParams
    }


    //UpdatePersonalProfileItem
    private val updatePersonalProfileItemResponse = MutableLiveData<Resource<String>>()

    fun updatePersonalProfileItem(imageUri:String,item: UserPersonalProfileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            updatePersonalProfileItemResponse.postValue(Resource.loading(null))
            val uc   = mainVMI.updatePersonalProfileItem(imageUri,item)
            if(uc != null){
                updatePersonalProfileItemResponse.postValue(Resource.success(uc))

            }else{
                updatePersonalProfileItemResponse.postValue(Resource.error(null))

            }

        }
    }
    fun onObserveUpdatePersonalProfileItemResponseData(): LiveData<Resource<String>> {
        return updatePersonalProfileItemResponse
    }

    //UpdateProfessionalProfileItem
    private val updateProfessionalProfileItemResponse = MutableLiveData<Resource<String>>()

    fun updateProfessionalProfileItem(imageUri:String,item: UserProfessionalProfileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            updateProfessionalProfileItemResponse.postValue(Resource.loading(null))
            val uc   = mainVMI.updateProfessionalProfileItem(imageUri,item)
            if(uc != null){
                updateProfessionalProfileItemResponse.postValue(Resource.success(uc))

            }else{
                updateProfessionalProfileItemResponse.postValue(Resource.error(null))

            }

        }
    }
    fun onObserveUpdateProfessionalProfileItemResponseData(): LiveData<Resource<String>> {
        return updateProfessionalProfileItemResponse
    }

    //GetUSerDetails
    suspend fun getUserProfileAsync(id:String): Resource<UserPersonalProfileModel> {
        return withContext(Dispatchers.IO) {
            try {
                val uc = mainVMI.getUserProfile(id)
                if (uc!= null) {
                    Resource.success(uc)
                } else {
                    Resource.error(null)
                }
            } catch (e: Exception) {
                Resource.error(null)
            }
        }
    }

    private val userProfileCache = MutableLiveData<UserPersonalProfileModel>()
    fun setUserProfileCache(data: UserPersonalProfileModel) {
        userProfileCache.value = data
    }
    fun getUserProfileCache(): LiveData<UserPersonalProfileModel> {
        return userProfileCache
    }


    suspend fun getAllProfessionalProfileItemsAsync(): Resource<List<UserProfessionalProfileModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val uc = mainVMI.getAllProfessionalProfileItemsAsync()
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
    private val allProfessionalProfileItemsCache = MutableLiveData<List<UserProfessionalProfileModel>>()
    fun setAllProfessionalProfileItemsCache(data: List<UserProfessionalProfileModel>) {
        allProfessionalProfileItemsCache.value = data
    }
    fun getAllProfessionalProfileItemsCache(): LiveData<List<UserProfessionalProfileModel>> {
        return allProfessionalProfileItemsCache
    }


    //GetUSerDetails
    suspend fun getRechargeItemAsync(id:String): Resource<RechargeItem> {
        return withContext(Dispatchers.IO) {
            try {
                val uc = mainVMI.getRechargeItem(id)
                if (uc!= null) {
                    Resource.success(uc)
                } else {
                    Resource.error(null)
                }
            } catch (e: Exception) {
                Resource.error(null)
            }
        }
    }

    private val getRechargeItemAsyncCache = MutableLiveData<RechargeItem>()
    fun setRechargeItemAsyncCache(data: RechargeItem) {
        getRechargeItemAsyncCache.value = data
    }
    fun getRechargeItemAsyncCache(): LiveData<RechargeItem> {
        return getRechargeItemAsyncCache
    }




}