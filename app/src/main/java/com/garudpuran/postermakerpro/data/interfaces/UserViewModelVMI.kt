package com.garudpuran.postermakerpro.data.interfaces

import android.net.Uri
import com.garudpuran.postermakerpro.ui.commonui.models.ProfileItemModel
import com.garudpuran.postermakerpro.utils.UiState

interface UserViewModelVMI {
    suspend fun updateProfile(id: String, mod: ProfileItemModel, onResult: (UiState<String>) -> Unit)

}