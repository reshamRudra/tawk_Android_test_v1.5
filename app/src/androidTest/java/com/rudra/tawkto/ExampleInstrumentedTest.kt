package com.rudra.tawkto

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rudra.tawkto.room.AppDatabase
import com.rudra.tawkto.room.UserDao
import com.rudra.tawkto.room.UserEntity
import org.junit.*

import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.rules.TestRule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        try {
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries().build()
        } catch (e: Exception) {
            Log.i("test", e.message)
        }
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAddingAndRetrievingData() {
        // 1 test calls the getAllByLoginId() method on  DAO that will get all of the current records having login="reshmast" in database.
        val preInsertRetrievedCategories = userDao.getAllByLoginId("reshmast")

        // 2 create an entity object and insert it into database.
        val listCategory = UserEntity(
            22743935,
            "reshmast",
            "MDQ6VXNlcjIyNzQzOTM1",
            "https://avatars.githubusercontent.com/u/22743935?v=4",
            "",
            "https://api.github.com/users/reshmast",
            "https://github.com/reshmast",
            "https://api.github.com/users/reshmast/followers",
            "https://api.github.com/users/reshmast/following{/other_user}",
            "https://api.github.com/users/reshmast/gists{/gist_id}",
            "https://api.github.com/users/reshmast/starred{/owner}{/repo}",
            "https://api.github.com/users/reshmast/subscriptions",
            "https://api.github.com/users/reshmast/orgs",
            "https://api.github.com/users/reshmast/repos",
            "https://api.github.com/users/reshmast/events{/privacy}",
            "https://api.github.com/users/reshmast/received_events",
            "User",
            false,
            "Resham Acharya",
            null,
            "",
            "Chandigarh",
            null,
            null,
            null,
            null,
            1,
            0,
            0,
            0,
            "2016-10-10T10:49:37Z",
            "2020-09-10T07:05:22Z",
            "Test User"
        )
        userDao.insert(listCategory)

       /* 3 Finally, perform some Assert.assertEquals
        calls on the result set after the
        record is added to ensure that a
        record was added. To do that,
        compare the size before and after
        adding a record to make sure that the
        difference is 1, and then look at
        the last record to ensure that its
        elements match the record was added.*/
        val postInsertRetrievedCategories = userDao.getAllByLoginId("reshmast")
        val sizeDifference = postInsertRetrievedCategories.size - preInsertRetrievedCategories.size
        Assert.assertEquals(1, sizeDifference)
        val retrievedCategory = postInsertRetrievedCategories.last()
        Assert.assertEquals("reshmast", retrievedCategory.login)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.rudra.tawkto", appContext.packageName)
    }
}