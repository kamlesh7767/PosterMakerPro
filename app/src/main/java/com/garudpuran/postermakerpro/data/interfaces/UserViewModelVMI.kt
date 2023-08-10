package com.garudpuran.postermakerpro.data.interfaces

import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel

interface UserViewModelVMI {
    suspend fun updateProfile(id: String, mod: UserPersonalProfileModel):String
    suspend fun updateProfileFields(id: String, paramMap:Map<String,Any>):String
    suspend fun getUserProfile(id: String):UserPersonalProfileModel?

    suspend fun updatePersonalProfileItem(imageUri: String, item: UserPersonalProfileModel):String?

}