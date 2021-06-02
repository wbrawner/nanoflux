package com.wbrawner.nanoflux.storage.dao

import androidx.room.*
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed

@Dao
interface EntryDao {
    @Transaction
    @Query("SELECT * FROM Entry")
    fun getAll(): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\"")
    fun getAllUnread(): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE id in (:ids)")
    fun getAllByIds(vararg ids: Long): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE feedId in (:ids)")
    fun getAllByFeedIds(vararg ids: Long): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\" AND feedId in (:ids)")
    fun getAllUnreadByFeedIds(vararg ids: Long): List<EntryAndFeed>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entries: List<Entry>)

    @Delete
    fun delete(entry: Entry)
}