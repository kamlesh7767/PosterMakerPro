package com.garudpuran.postermakerpro.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CategoryItem(
    var Id:String? = "",
    var title_eng:String = "",
    var title_mar:String = "",
    var title_hin:String = "",
    var visibility:Boolean? = true,
    @ServerTimestamp
    var creationDate: Date? = null
)
