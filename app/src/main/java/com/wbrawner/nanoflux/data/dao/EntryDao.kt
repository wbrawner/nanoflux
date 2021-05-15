package com.wbrawner.nanoflux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wbrawner.nanoflux.data.model.Entry
import com.wbrawner.nanoflux.data.model.Feed
import com.wbrawner.nanoflux.data.model.FeedCategoryIcon
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM Entry")
    fun getAll(): Flow<List<Entry>>

    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\"")
    fun getAllUnread(): Flow<List<Entry>>

    @Query("SELECT * FROM Entry WHERE id in (:ids)")
    fun getAllByIds(vararg ids: Long): List<Entry>

    @Query("SELECT * FROM Entry WHERE feedId in (:ids)")
    fun getAllByFeedIds(vararg ids: Long): Flow<List<Entry>>

    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\" AND feedId in (:ids)")
    fun getAllUnreadByFeedIds(vararg ids: Long): Flow<List<Entry>>

    @Insert
    fun insertAll(entries: List<Entry>)

    @Delete
    fun delete(entry: Entry)
}