package com.wbrawner.nanoflux.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wbrawner.nanoflux.storage.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM User WHERE id in (:ids)")
    suspend fun getAllByIds(vararg ids: Long): List<User>

    @Insert
    suspend fun insertAll(vararg users: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM User")
    suspend fun deleteAll()
}