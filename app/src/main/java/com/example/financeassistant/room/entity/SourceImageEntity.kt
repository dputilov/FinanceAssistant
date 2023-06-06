package com.example.financeassistant.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.financeassistant.room.database.RoomDatabaseSettings
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = RoomDatabaseSettings.ROOM_DB_SOURCE_IMAGE,
primaryKeys = ["uid", "owner_uid"])
data class SourceImageEntity(
    @ColumnInfo(name = "uid")
    @SerializedName("uid")
    var uid: String = "",

    @ColumnInfo(name = "owner_uid")
    @SerializedName("uid")
    var ownerUid: String = "",

    @ColumnInfo(name = "is_avatar")
    @SerializedName("isAvatar")
    var isAvatar: Boolean = false,

    @ColumnInfo(name = "source_url")
    @SerializedName("SourceUrl")
    var sourceUrl: String = "",

    @ColumnInfo(name = "file_name")
    @SerializedName("FileName")
    var filename : String = "",

    @ColumnInfo(name = "extension")
    @SerializedName("Extension")
    var extension: String = "",

    @ColumnInfo(name = "create_at")
    @SerializedName("CreateAt")
    var createAt: Date? = null,

    @ColumnInfo(name = "width")
    @SerializedName("Width")
    var width: Int = 0,

    @ColumnInfo(name = "height")
    @SerializedName("Height")
    var height: Int = 0,

    @ColumnInfo(name = "size")
    @SerializedName("Size")
    var size: Long = 0L
)