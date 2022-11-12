package com.wbrawner.nanoflux.storage.dao

import androidx.room.*
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT COUNT(*) FROM Entry")
    suspend fun count(): Long

    @Transaction
    @Query("SELECT * FROM Entry ORDER BY publishedAt DESC")
    fun observeAll(): Flow<List<EntryAndFeed>>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"READ\" ORDER BY publishedAt DESC")
    fun observeRead(): Flow<List<EntryAndFeed>>

    @Transaction
    @Query("SELECT * FROM Entry ORDER BY Entry.publishedAt DESC")
    fun observeStarred(): Flow<List<EntryAndFeed>>

    @Transaction
    @Query(
        """
        SELECT * FROM Entry 
        INNER JOIN Feed on Feed.id = Entry.feedId
        INNER JOIN Category on Category.id = Feed.categoryId
        WHERE Entry.status = "UNREAD" AND Feed.hideGlobally = 0 AND Category.hideGlobally = 0
        ORDER BY publishedAt DESC
        """
    )
    fun observeUnread(): Flow<List<EntryAndFeed>>

    @Transaction
    @Query("SELECT * FROM Entry ORDER BY publishedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun load(limit: Int, offset: Int): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"READ\" ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun loadRead(limit: Int, offset: Int): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE status = \"UNREAD\" ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun loadUnread(limit: Int, offset: Int): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE starred = 1 ORDER BY publishedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun loadStarred(limit: Int, offset: Int): List<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE id = :id")
    fun observe(id: Long): Flow<EntryAndFeed>

    @Transaction
    @Query("SELECT * FROM Entry WHERE id = :id")
    fun get(id: Long): EntryAndFeed?

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