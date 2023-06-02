package com.example.financeassistant.classes

import com.google.gson.annotations.SerializedName
import java.util.Date

class SourceImage(
    @SerializedName("PictureUid")
    var pictureUid: String = "",

    @SerializedName("SourceUrl")
    var sourceUrl: String = "",

    @SerializedName("Path")
    var path: String = "",

    @SerializedName("FileName")
    var filename : String = "",

    @SerializedName("Extension")
    var extension: String = "",

    @SerializedName("CreateAt")
    var createAt: Date? = null,

    @SerializedName("Size")
    var size: Long = 0L

)