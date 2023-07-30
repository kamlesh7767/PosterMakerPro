package com.garudpuran.postermakerpro.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.ui.commonui.models.ProfileItemModel
import com.garudpuran.postermakerpro.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val mainVMI: UserViewModelVMI
) : ViewModel() {

    fun updateUserDetails(id:String,userModel: ProfileItemModel,onResult: (UiState<String>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            mainVMI.updateProfile(id,userModel,onResult)
        }
    }



}