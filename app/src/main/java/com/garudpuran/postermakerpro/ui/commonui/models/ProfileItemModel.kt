package com.garudpuran.postermakerpro.ui.commonui.models

data class ProfileItemModel(
    var name: String = "",
    var mobile_number: String = "",
    var email: String = "",
    var website: String = "",
    var profile_image_url: String = "",
    var contactItems: List<ProfileContactModels>
)
