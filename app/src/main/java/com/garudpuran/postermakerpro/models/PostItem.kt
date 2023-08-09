package com.garudpuran.postermakerpro.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PostItem(
    var Id:String? = "",
    var title_eng:String = "",
    var title_mar:String = "",
    var title_hin:String = "",
    var image_url:String = "",
    var price:String = "",
    var categoryId:String = "",
    var subCategoryId:String = "",
    var downloads:Int = 0,
    var likes:Int = 0,
    var visibility:Boolean? = true,
    var paid:Boolean? = false,
    @ServerTimestamp
    var postDate: Date? = null

)
