package com.ivy.core.persistence

import androidx.room.Room
import com.ivy.common.test.AndroidTest
import com.ivy.common.test.testContext
import com.ivy.core.persistence.dao.TrnDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException


@AndroidTest
class RoomDbTest {
    private lateinit var trnDao: TrnDao
    private lateinit var db: IvyWalletDb

    @Before
    fun createDb() {
        val context = testContext()
        db = Room.inMemoryDatabaseBuilder(
            context, IvyWalletDb::class.java
        ).build()
        trnDao = db.trnDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        assert(trnDao.findAll().isEmpty())
    }
}