package com.wbrawner.nanoflux.storage.dao

import androidx.room.*
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT COUNT(*) FROM Entry")
    fun getCount(): Long

    @Transaction
    @Query("SELECT * FROM Entry ORDER BY publishedAt DESC")
    fun observeAll(): Flow<List<EntryAndFeed>>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\" ORDER BY publishedAt DESC")
    fun observeUnread(): Flow<List<EntryAndFeed>>

    @Transaction
    @Query("SELECT * FROM Entry ORDER BY publishedAt DESC")
    fun getAll(): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\" ORDER BY createdAt DESC")
    fun getAllUnread(): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE id in (:ids) ORDER BY createdAt DESC")
    fun getAllByIds(vararg ids: Long): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE feedId in (:ids) ORDER BY createdAt DESC")
    fun getAllByFeedIds(vararg ids: Long): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\" AND feedId in (:ids) ORDER BY createdAt DESC")
    fun getAllUnreadByFeedIds(vararg ids: Long): List<EntryAndFeed>

    @Query("SELECT id FROM Entry ORDER BY id DESC LIMIT 1")
    fun getLatestId(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entries: List<Entry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg entry: Entry)

    @Update
    fun update(entry: Entry)

    @Delete
    fun delete(entry: Entry)
}