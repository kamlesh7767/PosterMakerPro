package com.garudpuran.postermakerpro.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class SubCategoryItem(
    var Id:String? = "",
    var title_eng:String = "",
    var title_mar:String = "",
    var title_hin:String = "",
    var image_url:String = "",
    var categoryId :String? = "",
    var visibility:Boolean? = true,
    @ServerTimestamp
    var postDate: Date? = null

)
