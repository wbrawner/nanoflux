package com.wbrawner.nanoflux.storage.dao

import androidx.room.*
import com.wbrawner.nanoflux.storage.model.Feed
import com.wbrawner.nanoflux.storage.model.FeedCategoryIcon

@Dao
interface FeedDao {
    @Transaction
    @Query("SELECT * FROM Feed")
    fun getAll(): List<FeedCategoryIcon>

    @Transaction
    @Query("SELECT * FROM Feed WHERE id in (:ids)")
    fun getAllByIds(vararg ids: Long): List<FeedCategoryIcon>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(feeds: List<Feed>)

    @Delete
    fun delete(feed: Feed)
}