package com.ivy.core.persistence

import androidx.room.Room
import com.ivy.common.test.testContext
import org.junit.After
import org.junit.Before
import java.io.IOException


abstract class RoomDbTest {
    protected lateinit var db: IvyWalletDb

    abstract fun setUp(db: IvyWalletDb)

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            testContext(), IvyWalletDb::class.java
        ).build()
        setUp(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}