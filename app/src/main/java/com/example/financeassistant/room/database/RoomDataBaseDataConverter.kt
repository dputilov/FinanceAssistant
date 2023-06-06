package com.example.financeassistant.room.database

import androidx.room.TypeConverter
import com.example.financeassistant.classes.FlatPaymentOperationType
import com.example.financeassistant.classes.FlatPaymentType
import com.example.financeassistant.classes.HomeType
import java.util.Date

class RoomDataBaseDataConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromFlatPaymentType(data: FlatPaymentType) : Int {
        return data.type
    }

    @TypeConverter
    fun toFlatPaymentType(data: Int?) : FlatPaymentType {
        return FlatPaymentType.getById(data ?: 0)
    }

    @TypeConverter
    fun fromFlatPaymentOperationType(data: FlatPaymentOperationType) : Int {
        return data.type
    }

    @TypeConverter
    fun toFlatPaymentOperationType(data: Int?) : FlatPaymentOperationType {
        return FlatPaymentOperationType.getById(data ?: 0)
    }

    @TypeConverter
    fun fromHomeType(data: HomeType) : Int {
        return data.type
    }

    @TypeConverter
    fun toHomeType(data: Int?) : HomeType {
        return HomeType.getById(data ?: -1)
    }

}