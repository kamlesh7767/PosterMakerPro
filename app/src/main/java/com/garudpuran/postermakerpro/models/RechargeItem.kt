package com.garudpuran.postermakerpro.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class RechargeItem(
    var uid: String = "",
    var image_eng: String = "",
    var image_mar: String = "",
    var image_hin: String = "",
    var amount: Int = 0,
    var points: Int = 0,
    @ServerTimestamp
    var postDate: Date? = null
)
