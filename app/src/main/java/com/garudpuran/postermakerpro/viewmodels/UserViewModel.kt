package com.garudpuran.postermakerpro.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.utils.Resource
import com.garudpuran.postermakerpro.utils.ResponseStrings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            Log.d("RESPONSE_DATA2",uc)
            if(uc == ResponseStrings.SUCCESS){
                updateUserDetailsData.postValue(Resource.success(uc))
                Log.d("RESPONSE_DATA3",uc)
            }else{
                updateUserDetailsData.postValue(Resource.error(null))
                Log.d("RESPONSE_DATA4",uc)
            }

        }
    }
    fun onObserveUpdateUserDetailsData(): LiveData<Resource<String>> {
        return updateUserDetailsData
    }

    //GetUSerDetails
    private val getUserProfileData = MutableLiveData<Resource<UserPersonalProfileModel>>()

    fun getUserProfile(id:String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileData.postValue(Resource.loading(null))
            val uc   = mainVMI.getUserProfile(id)
            if(uc != null){
                getUserProfileData.postValue(Resource.success(uc))

            }else{
                getUserProfileData.postValue(Resource.error(null))

            }

        }
    }
    fun onObserveGetUserProfileData(): LiveData<Resource<UserPersonalProfileModel>> {
        return getUserProfileData
    }



}