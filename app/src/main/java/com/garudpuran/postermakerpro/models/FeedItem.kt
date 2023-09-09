package com.garudpuran.postermakerpro.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class FeedItem(
    var Id:String? = "",
    var title_eng:String = "",
    var title_mar:String = "",
    var title_hin:String = "",
    var image_url:String = "",
    var original_image_url:String = "",
    var createdByAdmin:Boolean = true,
    var categoryId:String = "",
    var subCategoryId:String = "",
    var postId:String = "",
    var type:Int = 0,
    var likes:Int = 0,
    var visibility:Boolean? = true,
    var userProfile:UserPersonalProfileModel? =null

)
