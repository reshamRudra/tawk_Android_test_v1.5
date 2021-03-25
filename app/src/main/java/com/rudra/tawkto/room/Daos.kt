package com.rudra.tawkto.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM list_users")
    fun getAll(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM list_users WHERE login = :login")
    fun getAllByLoginId(login: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(users: List<UserEntity>): List<Long>

    @Update
    fun update(vararg users: UserEntity)

    @Update
    fun update(users: List<UserEntity>)

    @Delete
    fun delete(user: UserEntity)

    @Transaction
    fun upsert(user: UserEntity) {
        val id = insert(user)
        if (id == -1L) {
            update(user)
        }
    }

    @Transaction
    fun upsert(users: List<UserEntity>) {
        val insertResult = insert(users)
        val updateList: MutableList<UserEntity> = ArrayList()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                val userProfile = getAllByLoginId(users[i].login!!)
                if (userProfile.size > 0) {
                    users[i].notes = userProfile[0].notes
                }
                updateList.add(users[i])
            }
        }
        if (!updateList.isEmpty()) {
            update(updateList)
        }
    }
}