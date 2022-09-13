package com.ivy.core.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.ivy.core.persistence.dao.TrnDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4ClassRunner::class)
class RoomDbTest {
    private lateinit var trnDao: TrnDao
    private lateinit var db: IvyWalletDb

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
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