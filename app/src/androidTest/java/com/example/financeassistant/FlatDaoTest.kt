package com.example.financeassistant

import android.util.Log
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.financeassistant.FlatTestHelper.Companion.TEST_TAG
import com.example.financeassistant.FlatTestHelper.Companion.createFlatItem
import com.example.financeassistant.FlatTestHelper.Companion.createFlatList
import com.example.financeassistant.FlatTestHelper.Companion.flatsAreIdentical
import com.example.financeassistant.FlatTestHelper.Companion.getRandomInt
import com.example.financeassistant.classes.HomeType.Automobile
import com.example.financeassistant.manager.RoomDatabaseManager
import com.example.financeassistant.room.dao.FlatDao
import com.example.financeassistant.room.database.AppDatabase
import com.example.financeassistant.room.entity.FlatEntity
import com.example.financeassistant.utils.getNewUid
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlatDaoTest {

    private var db: AppDatabase? = null
    private var flatDao: FlatDao? = null

    @Before
    @Throws(Exception::class)
    fun createDb() {
        db = inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        )
            .build()
        flatDao = db?.flatDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db!!.close()
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun whenInsertFlatThenReadTheSameOne() {
        val flats = createFlatList(1)
        flatDao?.insert(flats)
        val dbFlats = flatDao?.getAllTest()
        assertEquals(1, dbFlats?.size)
        assertTrue(flatsAreIdentical(flats[0], dbFlats!![0]))
    }

    @Test
    fun whenInsertSomeFlatThenCheckListSize() {
        val flatList = createFlatList(2)
        flatDao?.insert(flatList)

        val uid = getNewUid()
        val flat = createFlatItem(uid)
        flatDao?.insert(listOf(flat))

        flatDao?.insert(createFlatList(2))

        // Test
        val flatTestList = flatDao?.getAllTest()

        Log.d("ROOM_TEST", "Count = ${flatTestList?.size}")

        assertEquals(5, flatTestList?.size)

    }

    @Test
    @Throws(java.lang.Exception::class)
    fun whenUpdateFlatThenReadTheSameOne() {
        // Insert
        val flats = createFlatList(1)
        flatDao?.insert(flats)

        // Read and Update item
        val dbFlats = flatDao?.getAllTest()
        val dbTestFlat = dbFlats!![0].copy()

        dbTestFlat.adres = "${dbTestFlat.adres} ${dbTestFlat.adres} ${dbTestFlat.adres}"
        dbTestFlat.summa = getRandomInt().toDouble()
        dbTestFlat.summaArenda = getRandomInt().toDouble()
        dbTestFlat.type = Automobile

        flatDao?.update(listOf(dbTestFlat))

        // Read and check updated item
        val dbFlatsUpdated = flatDao?.getAllTest()

        assertEquals(1, dbFlatsUpdated?.size)
        assertTrue(flatsAreIdentical(dbTestFlat, dbFlatsUpdated!![0]))

        Log.d(TEST_TAG, "flat = ${dbFlats[0]} \n updated flat = ${dbFlatsUpdated[0]}")

    }
}