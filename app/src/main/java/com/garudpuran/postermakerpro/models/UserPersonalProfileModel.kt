package com.garudpuran.postermakerpro.models

data class UserPersonalProfileModel(
    var uid:String = "",
    var name:String = "",
    var profile_image_url:String = "",
    var mobile_number:String = "",
    var email:String = "",
    var points:Int = 0,
    var likedPosts: ArrayList<String> = ArrayList(),
    var recharges: ArrayList<SuccessfulRecharges> = ArrayList()
    )
