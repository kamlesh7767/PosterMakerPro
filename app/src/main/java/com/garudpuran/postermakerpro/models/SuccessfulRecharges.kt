package com.garudpuran.postermakerpro.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class SuccessfulRecharges(
    var uid: String = "",
    var rechargeItem: RechargeItem? =null,
    var dateTime: String? = ""
)






