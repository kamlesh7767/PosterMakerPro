package com.garudpuran.postermakerpro.data.interfaces

import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.models.UserProfessionalProfileModel

interface UserViewModelVMI {
    suspend fun updateProfile(id: String, mod: UserPersonalProfileModel):String
    suspend fun updateProfileFields(id: String, paramMap:Map<String,Any>):String
    suspend fun getUserProfile(id: String):UserPersonalProfileModel?
    suspend fun getRechargeItem(id: String):RechargeItem?

    suspend fun updatePersonalProfileItem(imageUri: String, item: UserPersonalProfileModel):String?
    suspend fun updateProfessionalProfileItem(imageUri: String, item: UserProfessionalProfileModel):String?

    suspend fun getAllProfessionalProfileItemsAsync():List<UserProfessionalProfileModel>

}