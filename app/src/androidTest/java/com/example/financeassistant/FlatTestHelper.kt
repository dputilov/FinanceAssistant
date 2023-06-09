package com.example.financeassistant

import com.example.financeassistant.room.entity.FlatEntity
import com.example.financeassistant.utils.getNewUid
import java.util.Date

class FlatTestHelper {

    companion object {

        const val TEST_TAG = "ROOM_TEST"

        fun createFlatItem(uid: String): FlatEntity {
            val flat = createRandomFlat()
            flat.uid = uid
            return flat
        }

        fun createFlatList(cnt: Int): List<FlatEntity> {
            val flatList = mutableListOf<FlatEntity>()
            for (i in 1..cnt) {
                val flat = createRandomFlat()
                flatList.add(flat)
            }
            return flatList
        }

        fun createRandomFlat(): FlatEntity {
            return FlatEntity(
                uid = getNewUid(),
                name = getRandomString("name"),
                adres = getRandomString("adres"),
                param = getRandomString("param"),
                purchaseDate = Date(),
                issueYear = getRandomInt(),
                creditUid = getNewUid()
            )
        }

        fun getRandomString(str: String, rangeStart: Int = 0, rangeEnd: Int = 100): String {
            return str + "_" + getRandomInt(rangeStart, rangeEnd).toString()
        }

        fun getRandomInt(rangeStart: Int = 0, rangeEnd: Int = 100): Int {
            return (rangeStart..rangeEnd).random()
        }

        fun flatsAreIdentical(flat1:FlatEntity, flat2:FlatEntity): Boolean {
            return flat1.equals(flat2)
        }
    }

}