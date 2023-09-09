package com.garudpuran.postermakerpro.models

data class SearchModel(
    var Id:String? = "",
    var title_eng:String = "",
    var title_mar:String = "",
    var title_hin:String = "",
    var categoryId :String? = "",
    var image_url:String = "",
    var subCategoryId:String = "",
    var type:String = "",
    var visibility:Boolean? = true,

)