package com.wbrawner.nanoflux.data.dao

import androidx.room.*
import com.wbrawner.nanoflux.data.model.Feed
import com.wbrawner.nanoflux.data.model.FeedCategoryIcon
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Transaction
    @Query("SELECT * FROM Feed")
    fun getAll(): Flow<List<FeedCategoryIcon>>

    @Transaction
    @Query("SELECT * FROM Feed WHERE id in (:ids)")
    fun getAllByIds(vararg ids: Long): List<FeedCategoryIcon>

    @Insert
    fun insertAll(feeds: List<Feed>)

    @Delete
    fun delete(feed: Feed)
}