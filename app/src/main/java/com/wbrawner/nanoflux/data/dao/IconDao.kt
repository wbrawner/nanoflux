package com.wbrawner.nanoflux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wbrawner.nanoflux.data.model.Feed

@Dao
interface IconDao {
    @Query("SELECT * FROM Icon")
    fun getAll(): List<Feed.Icon>

    @Query("SELECT * FROM Icon WHERE iconId in (:ids)")
    fun getAllByIds(vararg ids: Long): List<Feed.Icon>

    @Insert
    fun insertAll(vararg icons: Feed.Icon)

    @Delete
    fun delete(icon: Feed.Icon)
}