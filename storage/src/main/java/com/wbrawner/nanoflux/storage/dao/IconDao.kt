package com.wbrawner.nanoflux.storage.dao

import androidx.room.*
import com.wbrawner.nanoflux.storage.model.Feed
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    @Query("SELECT * FROM Icon")
    fun getAll(): Flow<List<Feed.Icon>>

    @Query("SELECT * FROM Icon WHERE id in (:ids)")
    fun getAllByIds(vararg ids: Long): List<Feed.Icon>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg icons: Feed.Icon)

    @Delete
    fun delete(icon: Feed.Icon)
}