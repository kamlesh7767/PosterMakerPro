package com.garudpuran.postermakerpro.data.interfaces

import com.garudpuran.postermakerpro.models.UserPersonalProfileModel

interface UserViewModelVMI {
    suspend fun updateProfile(id: String, mod: UserPersonalProfileModel):String
    suspend fun getUserProfile(id: String):UserPersonalProfileModel?

}